import fetch from "node-fetch";
import * as tar from "tar-stream";
import gunzipMaybe from "gunzip-maybe";

export async function GET(request: Request) {
  const repoUrl = new URL(request.url).searchParams.get("repoUrl");
  const response = await fetch(`${repoUrl}/archive/refs/heads/master.tar.gz`);

  const extract = tar.extract({});
  const gunzip = gunzipMaybe();

  response.body!.pipe(gunzip).pipe(extract);

  const files: {
    path: string;
    content: string;
  }[] = [];

  for await (const entry of extract) {
    if (
      entry.header.type === "file" &&
      entry.header.name.includes("/input/cql/")
    ) {
      const content: Uint8Array[] = [];
      for await (const chunk of entry) {
        content.push(chunk);
      }
      files.push({
        path: entry.header.name,
        content: Buffer.concat(content).toString(),
      });
    }
    entry.resume();
  }
  return new Response(JSON.stringify(files), {
    headers: {
      "content-type": "application/json",
    },
  });
}
