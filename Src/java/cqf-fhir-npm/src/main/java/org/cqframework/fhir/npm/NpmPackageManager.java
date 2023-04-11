package org.cqframework.fhir.npm;

import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.r5.context.IWorkerContext;
import org.hl7.fhir.r5.model.ImplementationGuide;
import org.hl7.fhir.utilities.VersionUtilities;
import org.hl7.fhir.utilities.npm.FilesystemPackageCacheManager;
import org.hl7.fhir.utilities.npm.NpmPackage;
import org.hl7.fhir.utilities.npm.ToolsVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NpmPackageManager implements IWorkerContext.ILoggingService {
   private static final Logger logger = LoggerFactory.getLogger(NpmPackageManager.class);
   private final ImplementationGuide sourceIg;
   private final FilesystemPackageCacheManager fspcm;
   private final List<NpmPackage> npmList;

   public NpmPackageManager(ImplementationGuide sourceIg) {
      this.sourceIg = sourceIg;

      try {
         // userMode indicates whether the packageCache is within the working directory or in the user home
         fspcm = new FilesystemPackageCacheManager(true, ToolsVersion.TOOLS_VERSION);
      }
      catch (IOException e) {
         String message = "Error creating the FilesystemPackageCacheManager: " + e.getMessage();
         logErrorMessage(message);
         throw new NpmPackageManagerException(message, e);
      }

      npmList = new ArrayList<>();

      try {
         loadDependencies();
      } catch (Exception e) {
         logErrorMessage(e.getMessage());
         throw new NpmPackageManagerException(e.getMessage());
      }
   }

   public void loadDependencies() throws IOException {
      for (var fhirVersion : sourceIg.getFhirVersion()) {
         String coreFhirVersion = VersionUtilities.packageForVersion(fhirVersion.getCode());
         logMessage("Loading core FHIR version " + coreFhirVersion);
         npmList.add(fspcm.loadPackage(coreFhirVersion, fhirVersion.getCode()));
      }
      for (var dependency : sourceIg.getDependsOn()) {
         NpmPackage dependencyPackage = null;
         if (dependency.hasPackageId() && !hasPackage(dependency.getPackageId(), false)) {
            logMessage("Loading package: " + dependency.getPackageId());
            dependencyPackage = fspcm.loadPackage(dependency.getPackageId(), dependency.hasVersion() ? dependency.getVersion() : "current");
            npmList.add(dependencyPackage);
         }
         else if (dependency.hasUri() && !hasPackage(dependency.getUri(), true)) {
            IdDt id = new IdDt(dependency.getUri());
            logMessage("Loading package: " + id.getIdPart());
            dependencyPackage = fspcm.loadPackage(id.getIdPart(), dependency.hasVersion() ? dependency.getVersion() : "current");
            npmList.add(dependencyPackage);
         }
         else {
            String dependencyIdentifier = dependency.hasId() ? dependency.getId() : "";
            logWarningMessage("Dependency " +dependencyIdentifier+ "missing packageId and uri, so can't be referred to in markdown in the IG");
         }

         if (dependencyPackage != null) {
            loadDependencies(dependencyPackage);
         }
      }
   }

   public void loadDependencies(NpmPackage parentPackage) throws IOException {
      for (String dependency : parentPackage.dependencies()) {
         if (hasPackage(dependency, false)) continue;
         logMessage("Loading package: " + dependency);
         NpmPackage childPackage = fspcm.loadPackage(dependency);
         npmList.add(childPackage);
         if (!childPackage.dependencies().isEmpty()) {
            loadDependencies(childPackage);
         }
      }
   }

   public boolean hasPackage(String packageId, boolean isUrl) {
      for (NpmPackage npmPackage : npmList) {
         if (!isUrl) {
            return npmPackage.getNpm().has("name")
                    && packageId.startsWith(npmPackage.getNpm().get("name").getAsString());
         }
         else {
            return npmPackage.getNpm().has("canonical")
                    && packageId.equals(npmPackage.getNpm().get("canonical").getAsString());
         }
      }
      return false;
   }

   public List<NpmPackage> getNpmList() {
      return npmList;
   }

   public ImplementationGuide getSourceIg() {
      return sourceIg;
   }

   @Override
   public void logMessage(String message) {
      logger.info(message);
   }

   @Override
   public void logDebugMessage(LogCategory category, String message) {
      logger.debug(message);
   }

   public void logWarningMessage(String message) {
      logger.warn(message);
   }

   public void logErrorMessage(String message) {
      logger.error(message);
   }
}
