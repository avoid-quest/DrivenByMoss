# Driven by Avoid
Bitwig Studio extension for DualSense support.

### Building and Installing the extension

Users should download and install the version from the
[main site](http://www.mossgrabers.de/Software/Bitwig/Bitwig.html).
These directions are for developers to test changes prior to release.

1. Install [mise](https://mise.jdx.dev/).
2. Run `mise install` in this repo's root.
3. Run `mise run toolchain:verify` to confirm the project-local Java and Maven toolchain.
4. Run `mise run build`.
5. Follow [installation instructions] in the included manual for further steps.

Java and Maven are selected by mise for this project, so they do not need to be
installed globally. Shareable build outputs are written to `target/`, including
`target/DrivenByAvoid.bwextension` and `target/DrivenByAvoid-*-Bitwig.zip`.
