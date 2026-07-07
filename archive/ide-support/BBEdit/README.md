# NAME

BBEdit\_LM\_CQL.plist - BBEdit Codeless Language Module for HL7 Clinical Quality Language (CQL)

# DESCRIPTION

The XML/plain-text file ```BBEdit_LM_CQL.plist``` is a Codeless Language
Module for the Mac OS text editor BBEdit
[https://www.barebones.com/products/bbedit/](
https://www.barebones.com/products/bbedit/).

To use it, simply copy that file into this directory (which you might have
to create):

    ~/Library/Application Support/BBEdit/Language Modules

... and BBEdit will load it when it is next launched.

The CLM empowers BBEdit to syntax color CQL source code, and to scan it for
major entity (eg, routine and type) declarations so you can easily jump
around to those in your CQL source code files.

The CLM will automatically be applied to any text files with the filename
extension ".cql" but you can apply it to any file.

This CLM should be considered beta quality and may not handle some corner
case syntax.  A known gap is that DateTime/Time/Quantity value literals are
not yet syntax colored like string/number/etc literals are.

See also [https://www.barebones.com/support/develop/clm.html](
https://www.barebones.com/support/develop/clm.html) for a CLM reference,
should you want to improve on this CLM or make other ones.

# AUTHORS

Darren Duncan - darren@databaseconsultinggroup.com
