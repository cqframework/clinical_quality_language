export function fetchSync(url: string): string | null {
  const xhr = new XMLHttpRequest();
  xhr.open("GET", url, false);
  xhr.send(null);
  if (xhr.status === 200) {
    return xhr.responseText;
  } else {
    return null;
  }
}

export async function readFile(handle: FileSystemFileHandle) {
  try {
    const file = await handle.getFile();
    return file.text();
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (e) {
    return null;
  }
}
