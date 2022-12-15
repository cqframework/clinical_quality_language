package org.opencds.cqf.cql.engine.fhir.retrieve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterMap;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.api.SearchStyleEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.param.TokenParam;

public class RestFhirRetrieveProvider extends SearchParamFhirRetrieveProvider {

	private static final SearchStyleEnum DEFAULT_SEARCH_STYLE = SearchStyleEnum.GET;

	protected IGenericClient fhirClient;
	private SearchStyleEnum searchStyle;

    public RestFhirRetrieveProvider(SearchParameterResolver searchParameterResolver, IGenericClient fhirClient) {
        super(searchParameterResolver);
        this.fhirClient = fhirClient;
        this.searchStyle = DEFAULT_SEARCH_STYLE;
    }

    public RestFhirRetrieveProvider(SearchParameterResolver searchParameterResolver, ModelResolver modelResolver, IGenericClient fhirClient) {
        super(searchParameterResolver, modelResolver);
        // TODO: Figure out how to validate that the searchParameterResolver and the
        // client are on the same version of FHIR.
        this.fhirClient = fhirClient;
        this.searchStyle = DEFAULT_SEARCH_STYLE;
    }

	public void setSearchStyle(SearchStyleEnum value) {
		this.searchStyle = value;
	}

	public SearchStyleEnum getSearchStyle() {
		return this.searchStyle;
	}

	@Override
	protected Iterable<Object> executeQueries(String dataType, List<SearchParameterMap> queries) {
		if (queries == null || queries.isEmpty()) {
			return Collections.emptyList();
		}

		List<Object> objects = new ArrayList<>();
		List<IBaseBundle> bundles = new ArrayList<>();
		for (SearchParameterMap map : queries) {
			IBaseResource result = this.executeQuery(dataType, map);
			if (result instanceof IBaseBundle) {
				bundles.add((IBaseBundle) result);
			} else {
				objects.add(result);
			}
		}

		// TODO: evaluate this lazily in case the engine only needs the first element
		for (IBaseBundle b : bundles) {
			FhirBundleCursor cursor = new FhirBundleCursor(fhirClient, b, dataType);
			cursor.forEach(objects::add);
		}

		return objects;
	}

	protected IBaseResource executeQuery(String dataType, SearchParameterMap map) {
		if (map.containsKey("_id")) {
			return this.queryById(dataType, map);
		} else {
			IQuery<IBaseBundle> search = this.fhirClient.search().forResource(dataType);

			Map<String, List<IQueryParameterType>> flattenedMap = new HashMap<>();
			for (Map.Entry<String, List<List<IQueryParameterType>>> entry : map.entrySet()) {
				String name = entry.getKey();
				if (name == null) {
					continue;
				}

				List<List<IQueryParameterType>> value = entry.getValue();
				if (value == null || value.isEmpty()) {
					continue;
				}

				List<IQueryParameterType> flattened = new ArrayList<>();

				for (List<IQueryParameterType> subList : value) {

					if (subList == null || subList.isEmpty()) {
						continue;
					}

					if (subList.size() == 1) {
						flattened.add(subList.get(0));
						continue;
					}

					// Sublists are logical "Ors"
					// The only "Or" supported from the engine are tokens at the moment.
					// So this is a hack to add them to a "criterion", which is the
					// only way the HAPI POST api supports passing them.
					IQueryParameterType first = subList.get(0);
					if (first instanceof TokenParam) {
						TokenClientParam tcp = new TokenClientParam(name);

						IBaseCoding[] codings = this.toCodings(subList);

						ICriterion<?> criterion = tcp.exactly().codings(codings);

						search = search.where(criterion);
					} else {
						flattened.addAll(subList);
					}
				}

				flattenedMap.put(name, flattened);
			}

			if (getPageSize() != null) {
			    search.count(getPageSize());
			}

			return search.where(flattenedMap).usingStyle(this.searchStyle).execute();
		}
	}

	protected IBaseResource queryById(String dataType, SearchParameterMap map) {
		if (map.entrySet().size() > 1) {
			throw new IllegalArgumentException(String
					.format("Error querying %s. Queries by id must not have any other search criteria.", dataType));
		}

		List<IQueryParameterType> tokenList = map.get("_id").get(0);
		if (tokenList == null || tokenList.isEmpty()) {
			throw new IllegalArgumentException(
					String.format("Error querying %s. Attempted query by id but no id was specified.", dataType));
		}

		if (tokenList.size() > 1) {
			throw new IllegalArgumentException(String
					.format("Error querying %s. Attempted query by id but multiple ids were specified.", dataType));
		}

		IQueryParameterType param = tokenList.get(0);
		if (!(param instanceof TokenParam)) {
			throw new IllegalArgumentException(String
					.format("Error querying %s. Attempted query by id but a non-token parameter was given.", dataType));
		}

		String id = ((TokenParam) param).getValue();

		if (id == null) {
			throw new IllegalArgumentException(
					String.format("Error querying %s. Attempted query by id but id was null.", dataType));
		}

		return queryById(dataType, id);
	}

	protected IBaseResource queryById(String dataType, String id) {
		return this.fhirClient.read().resource(dataType).withId(id).execute();
	}

	protected IBaseCoding[] toCodings(List<IQueryParameterType> codingList) {
		List<IBaseCoding> codings = new ArrayList<>();

		BiFunction<String, String, IBaseCoding> codingConverter;

		switch (this.fhirClient.getFhirContext().getVersion().getVersion()) {
			case DSTU3:
				codingConverter = (system, code) -> new org.hl7.fhir.dstu3.model.Coding(system, code, null);
				break;
			case R4:
				codingConverter = (system, code) -> new org.hl7.fhir.r4.model.Coding(system, code, null);
				break;
			default:
				throw new IllegalArgumentException("Unhandled FHIR version");
		}

		for (IQueryParameterType param : codingList) {
			TokenParam token = (TokenParam) param;

			IBaseCoding coding = codingConverter.apply(token.getSystem(), token.getValue());

			codings.add(coding);
		}

		return codings.toArray(new IBaseCoding[codings.size()]);
	}
}
