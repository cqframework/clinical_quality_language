import { Nullable } from "@/shared";
import { supportedModels } from "@/cql/supported-models";
import { fetchSync } from "@/cql/utils";

const fetchedModels: {
  id: string;
  system: Nullable<string>;
  version: Nullable<string>;
  xml: string | null;
}[] = [];

export function getModelXml(
  id: string,
  system: Nullable<string>,
  version: Nullable<string>,
  sync: boolean,
  log: (message: string) => void,
  rerun: () => void,
) {
  const fetchedModel = fetchedModels.find(
    (_) => _.id === id && _.system === system && _.version === version,
  );
  if (fetchedModel) {
    return fetchedModel.xml;
  }
  const supportedModel = supportedModels.find(
    (_) => _.id === id && _.system === system && _.version === version,
  );
  if (supportedModel) {
    if (sync) {
      log(
        `INFO Fetching model with id=${id}, system=${system}, version=${version} synchronously from url=${supportedModel.url}...`,
      );
      const xml = fetchSync(supportedModel.url);
      if (xml === null) {
        log(`WARN Couldn't fetch model from url=${supportedModel.url}.`);
      } else {
        log(
          `INFO Fetched model with id=${id}, system=${system}, version=${version} successfully.`,
        );
      }
      fetchedModels.push({
        id: supportedModel.id,
        system: supportedModel.system,
        version: supportedModel.version,
        xml,
      });
      return xml;
    } else {
      log(
        `INFO Fetching model with id=${id}, system=${system}, version=${version} asynchronously from url=${supportedModel.url}...`,
      );
      (async () => {
        const response = await fetch(supportedModel.url);
        const xml = response.ok ? await response.text() : null;
        if (xml === null) {
          log(`WARN Couldn't fetch model from url=${supportedModel.url}.`);
        } else {
          log(
            `INFO Fetched model with id=${id}, system=${system}, version=${version} successfully.`,
          );
        }
        fetchedModels.push({
          id: supportedModel.id,
          system: supportedModel.system,
          version: supportedModel.version,
          xml,
        });
        log("INFO Rerunning...");
        rerun();
      })();
      throw "INFO Model is being fetched asynchronously. Will rerun when fetch completes.";
    }
  }
  log(
    `WARN Model with id=${id}, system=${system}, version=${version} is not in the list of supported models.`,
  );
  return null;
}
