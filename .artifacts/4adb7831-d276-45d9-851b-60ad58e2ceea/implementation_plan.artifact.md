# App Redesign: "The Forge & Brotherhood"

This plan outlines the visual overhaul of the MVDM app to better reflect a "Forge" and "Brotherhood" aesthetic. We will move away from the warm brown/gold theme toward a more industrial, heavy-duty, and "metal-and-fire" look.

## User Review Required

> [!IMPORTANT]
> This change will significantly alter the current look. We are moving from "Elegant Gold" to "Industrial Forge".

## Proposed Changes

### 1. Color Palette Update
We will redefine the design tokens in `Color.kt` to focus on charcoals, steels, and glowing ember oranges.

#### [MODIFY] [Color.kt](file:///C:/Users/info/AndroidStudioProjects/MyApplication/app/src/main/java/com/test/myapplication/ui/theme/Color.kt)
- Introduce `ForgeCoal` (Deep Background)
- Introduce `ForgeSteel` (Card/Surface)
- Introduce `ForgeEmber` (Primary Action Color)
- Update `MvmGold` to a more weathered/antique version.

### 2. Typography & Iconography
#### [MODIFY] [Type.kt](file:///C:/Users/info/AndroidStudioProjects/MyApplication/app/src/main/java/com/test/myapplication/ui/theme/Type.kt)
- Update `Typography` to use higher weights and increased letter-spacing for headers to create a "carved in stone/metal" feel.

### 3. Component Styling
#### [MODIFY] [MainActivity.kt](file:///C:/Users/info/AndroidStudioProjects/MyApplication/app/src/main/java/com/test/myapplication/MainActivity.kt)
- **Buttons**: Update `MvmButton` to have a more solid, heavy appearance (less rounded, maybe a subtle stroke).
- **Cards**: Use the new `ForgeSteel` for surfaces.
- **Header**: Use `ForgeCoal` for the main header background.
- **Backgrounds**: Use gradients that mimic a dark workshop or glowing forge.

---

## Verification Plan

### Automated Tests
- Run `gradlew :app:assembleDebug` to ensure no resource or code breaks.

### Manual Verification
- Review updated previews of `WelcomeScreen` and `HomeScreen` using the `render_compose_preview` tool.
- Deploy to an emulator to verify the "feel" of the navigation and transitions.
