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

public class NewNpmPackageManager implements IWorkerContext.ILoggingService {
   private static final Logger logger = LoggerFactory.getLogger(NewNpmPackageManager.class);
   private final ImplementationGuide sourceIg;
   private final FilesystemPackageCacheManager fspcm;
   private final List<NpmPackage> npmList;

   public NewNpmPackageManager(ImplementationGuide sourceIg) throws IOException {
      this.sourceIg = sourceIg;

      try {
         // userMode indicates whether the packageCache is within the working directory or in the user home
         fspcm = new FilesystemPackageCacheManager(true, ToolsVersion.TOOLS_VERSION);
      }
      catch (IOException e) {
         String message = "error creating the FilesystemPackageCacheManager";
         logMessage(message);
         throw new NpmPackageManagerException(message, e);
      }

      npmList = new ArrayList<>();
      loadDependencies();
   }

   public void loadDependencies() throws IOException {
      for (var fhirVersion : sourceIg.getFhirVersion()) {
         npmList.add(fspcm.loadPackage(VersionUtilities.packageForVersion(fhirVersion.getCode()), fhirVersion.getCode()));
      }
      for (var dependency : sourceIg.getDependsOn()) {
         NpmPackage dependencyPackage = null;
         if (dependency.hasPackageId()) {
            dependencyPackage = fspcm.loadPackage(dependency.getPackageId(), dependency.hasVersion() ? dependency.getVersion() : "current");
            npmList.add(dependencyPackage);
         }
         else if (dependency.hasUri()) {
            IdDt id = new IdDt(dependency.getUri());
            dependencyPackage = fspcm.loadPackage(id.getIdPart(), dependency.hasVersion() ? dependency.getVersion() : "current");
            npmList.add(dependencyPackage);
         }
         else {
            logMessage("Dependency missing packageId and uri, so can't be referred to in markdown in the IG");
         }

         if (dependencyPackage != null) {
            loadDependencies(dependencyPackage);
         }
      }
   }

   public void loadDependencies(NpmPackage parentPackage) throws IOException {
      for (String dependency : parentPackage.dependencies()) {
         NpmPackage childPackage = fspcm.loadPackage(dependency);
         npmList.add(childPackage);
         if (!childPackage.dependencies().isEmpty()) {
            loadDependencies(childPackage);
         }
      }
   }

   public List<NpmPackage> getNpmList() {
      return npmList;
   }

   public ImplementationGuide getSourceIg() {
      return sourceIg;
   }

   @Override
   public void logMessage(String message) {
      logger.warn(message);
   }

   @Override
   public void logDebugMessage(LogCategory category, String message) {
      logger.debug(message);
   }
}
