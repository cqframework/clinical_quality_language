import {
  Nullable,
  playgroundLibraryName,
  TLibrarySource,
  TMountedDir,
} from "@/shared";
import { fetchSync, readFile } from "@/cql/utils";

const fetchedLibraries: {
  id: string;
  system: Nullable<string>;
  version: Nullable<string>;
  cql: string | null;
}[] = [];

export function getLibraryCql(
  id: string,
  system: Nullable<string>,
  version: Nullable<string>,
  playgroundCql: string,
  librarySource: TLibrarySource,
  baseUrl: string,
  mountedDir: TMountedDir | null,
  sync: boolean,
  log: (message: string) => void,
  rerun: () => void,
) {
  if (id === playgroundLibraryName) {
    log(
      `INFO Requested library with id=${id}, system=${system}, version=${version}. Returned editor content.`,
    );
    return playgroundCql;
  }

  const fetchedLibrary = fetchedLibraries.find(
    (_) => _.id === id && _.system === system && _.version === version,
  );
  if (fetchedLibrary) {
    return fetchedLibrary.cql;
  }
  if (librarySource === "local") {
    if (mountedDir) {
      const dirHandle = mountedDir.handle;
      const fileName = `${id}.cql`;
      const file = mountedDir.files.find((_) => _.handle.name === fileName);
      if (file) {
        log(
          `INFO Reading library with id=${id}, system=${system}, version=${version} asynchronously from local file=${dirHandle.name}/${fileName}...`,
        );
        (async () => {
          const cql = await readFile(file.handle);
          if (cql === null) {
            log(
              `WARN Couldn't read library from local file=${dirHandle.name}/${fileName}.`,
            );
          } else {
            log(
              `INFO Read library with id=${id}, system=${system}, version=${version} successfully.`,
            );
          }
          fetchedLibraries.push({
            id,
            system,
            version,
            cql,
          });
          log("INFO Rerunning...");
          rerun();
        })();
        throw "INFO Library is being read asynchronously from local file system. Will rerun when reading completes.";
      }

      log(
        `WARN Library with id=${id}, system=${system}, version=${version} not found in mounted directory=${dirHandle.name}.`,
      );
      return null;
    }
    log(
      `WARN Library with id=${id}, system=${system}, version=${version} cannot be read from local file system because no directory is mounted.`,
    );
    return null;
  }

  const url = `${baseUrl}${id}.cql`;

  if (sync) {
    log(
      `INFO Fetching library with id=${id}, system=${system}, version=${version} synchronously from url=${url}...`,
    );
    const cql = fetchSync(url);
    if (cql === null) {
      log(`WARN Couldn't fetch library from url=${url}.`);
    } else {
      log(
        `INFO Fetched library with id=${id}, system=${system}, version=${version} successfully.`,
      );
    }
    fetchedLibraries.push({
      id,
      system,
      version,
      cql,
    });
    return cql;
  } else {
    log(
      `INFO Fetching library with id=${id}, system=${system}, version=${version} asynchronously from url=${url}...`,
    );
    (async () => {
      const response = await fetch(url);
      const cql = response.ok ? await response.text() : null;
      if (cql === null) {
        log(`WARN Couldn't fetch library from url=${url}.`);
      } else {
        log(
          `INFO Fetched library with id=${id}, system=${system}, version=${version} successfully.`,
        );
      }
      fetchedLibraries.push({
        id,
        system,
        version,
        cql,
      });
      log("INFO Rerunning...");
      rerun();
    })();
    throw "INFO Library is being fetched asynchronously. Will rerun when fetch completes.";
  }
}
