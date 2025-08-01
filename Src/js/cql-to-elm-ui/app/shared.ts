export type TCompileCqlArgs = {
  cql: string;
  useWasm: boolean;
  enableAnnotations: boolean;
  enableLocators: boolean;
  outputContentType: "json" | "xml";
  baseUrl: string;
};

export type TOutput =
  | {
      type: "log";
      log: string;
    }
  | {
      type: "elm";
      contentType: "json" | "xml";
      elm: string;
    };
