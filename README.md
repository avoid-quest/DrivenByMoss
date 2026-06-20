# Driven by Avoid
Bitwig Studio extension for using a Sony DualSense controller as a configurable
Bitwig controller.

This fork is based on [DrivenByMoss](https://github.com/git-moss/DrivenByMoss),
originally authored by Jürgen Moßgraber. Fork-specific changes are maintained
by Driven by Avoid contributors and remain licensed under LGPLv3; see
[LICENSE](LICENSE).

## How this fork differs

DrivenByMoss is a broad controller-extension project. This fork currently
focuses on one use case: a DualSense controller connected to Bitwig Studio.

- The built extension is named `DrivenByAvoid`.
- The Bitwig service registration is reduced to the DualSense extension only.
- The new DualSense extension reads wired USB HID input directly.
- DualSense buttons, sticks, analog triggers, touch points, gyro, and
  accelerometer controls can be assigned in Bitwig preferences.
- Assignable functions include MIDI notes, MIDI CC, pitch bend, note repeat,
  track and clip navigation, new/launch clip, transport play/stop, and
  metronome toggle.
- Default mappings are provided for basic track/clip navigation, transport,
  pitch bend, note repeat, and common MIDI CC controls.
- The build uses `mise` to pin Java and Maven versions for repeatable local
  and CI builds.
- Parser and controller-definition tests were added for the DualSense support.

This fork is not intended to replace upstream DrivenByMoss for the many other
controllers supported there. If you need Push, Launchpad, MCU, Maschine, OSC,
or the rest of the upstream controller set, use the upstream project.

## Current scope

The current DualSense implementation targets the wired USB input report for
Sony DualSense devices with vendor ID `0x054C` and product ID `0x0CE6`.
Bluetooth input, haptics, lightbar control, speaker output, and adaptive trigger
output are not implemented in this fork yet.

## Building and installing

These directions are for developers to build and test this fork.

1. Install [mise](https://mise.jdx.dev/).
2. Run `mise install` in this repo's root.
3. Run `mise run toolchain:verify` to confirm the project-local Java and Maven toolchain.
4. Run `mise run build`.
5. Copy `target/DrivenByAvoid.bwextension` into Bitwig Studio's Extensions
   folder, then add the `Sony / DualSense` controller in Bitwig Studio.

Java and Maven are selected by mise for this project, so they do not need to be
installed globally. Shareable build outputs are written to `target/`, including
`target/DrivenByAvoid.bwextension` and `target/DrivenByAvoid-*-Bitwig.zip`.

## License and attribution

The fork keeps the upstream LGPLv3 license terms and preserves upstream
copyright attribution. Files newly created for this fork identify Driven by Avoid
contributors as the maintainers, and upstream files changed by this fork include
a fork modification notice.
