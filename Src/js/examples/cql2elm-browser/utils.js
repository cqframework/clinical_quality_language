export function fetchSync(url) {
  const xhr = new XMLHttpRequest();
  xhr.open("GET", url, false);
  xhr.send(null);
  return xhr.responseText;
}
