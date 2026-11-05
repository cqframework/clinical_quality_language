// This fixes https://github.com/Kotlin/kotlinx-io/issues/345

import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
global.require = require;
