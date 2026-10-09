#!/usr/bin/env python3
import os
import re

root = r"C:\Users\Jadj\AndroidStudioProjects\TogoFood\app\src\main\java\com\example\togofood\ui\screens"

# Files to process
auth_files = [
    "auth/LoginScreen.kt",
    "auth/OtpVerificationBottomSheet.kt",
    "auth/RegisterScreen.kt",
    "auth/VendorOnboardingSections.kt",
]

sellerhub_files = [
    "sellerhub/SellerHubScreen.kt",
    "sellerhub/SellerPrepEditScreen.kt",
    "sellerhub/SellerProfileEditScreen.kt",
    "sellerhub/SellerScheduleScreen.kt",
]

all_files = [os.path.join(root, f) for f in auth_files + sellerhub_files]

# Replacement patterns
replacements = [
    # Background colors
    (r'\.background\(Color\.White\)', '.background(MaterialTheme.colorScheme.background)'),
    (r'\.background\(PageBg\)', '.background(MaterialTheme.colorScheme.background)'),
    (r'\.background\(CardBg\)', '.background(MaterialTheme.colorScheme.surface)'),
    (r'\.background\(SurfaceBg\)', '.background(MaterialTheme.colorScheme.surfaceContainer)'),
    (r'\.background\(SoftCream\)', '.background(MaterialTheme.colorScheme.surfaceContainer)'),
    (r'\.background\(Color\(0xFFFAFAFA\)\)', '.background(MaterialTheme.colorScheme.background)'),
    (r'\.background\(Color\(0xFFF5F5F5\)\)', '.background(MaterialTheme.colorScheme.surfaceContainer)'),
    (r'\.background\(Color\(0xFFFFF3E8\)\)', '.background(MaterialTheme.colorScheme.primaryContainer)'),
    (r'\.background\(Color\(0xFFFFF8E1\)\)', '.background(MaterialTheme.colorScheme.primaryContainer)'),
    (r'\.background\(Color\(0xFFE3F2FD\)\)', '.background(MaterialTheme.colorScheme.secondaryContainer)'),
    (r'\.background\(Color\(0xFFE8F5E9\)\)', '.background(MaterialTheme.colorScheme.tertiaryContainer)'),
    (r'\.background\(Color\(0xFFFBE9E7\)\)', '.background(MaterialTheme.colorScheme.primaryContainer)'),
    (r'\.background\(Color\(0xFFE0E0E0\)\)', '.background(MaterialTheme.colorScheme.surfaceContainerHigh)'),
    (r'\.background\(Color\(0xFFF0F0F0\)\)', '.background(MaterialTheme.colorScheme.surfaceContainerHigh)'),
    (r'\.background\(Color\(0xFFFFEDE3\)\)', '.background(MaterialTheme.colorScheme.primaryContainer)'),
    (r'\.background\(Color\(0xFFFBE9E7\)\)', '.background(MaterialTheme.colorScheme.primaryContainer)'),
    (r'\.background\(HeroPastel\)', '.background(MaterialTheme.colorScheme.primaryContainer)'),
    (r'\.background\(WarningBg\)', '.background(MaterialTheme.colorScheme.secondaryContainer)'),
    (r'\.background\(OpenGreen\.copy\(alpha = 0\.10f\)\)', '.background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f))'),
    (r'\.background\(if \(isOpen\) OpenGreen\.copy\(alpha = 0\.10f\) else SurfaceBg\)', '.background(if (isOpen) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surfaceContainer)'),
    (r'\.background\(if \(selected\) BrandOrangeLight else SurfaceBg\)', '.background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer)'),
    (r'\.background\(if \(selected\) MaterialTheme\.colorScheme\.primary\.copy\(alpha = 0\.12f\) else SurfaceBg\)', '.background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceContainer)'),
    (r'\.background\(if \(selected\) MaterialTheme\.colorScheme\.primary\.copy\(alpha = 0\.14f\) else SurfaceBg\)', '.background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceContainer)'),
    (r'\.background\(if \(acceptTerms\) MaterialTheme\.colorScheme\.primaryContainer else SurfaceBg\)', '.background(if (acceptTerms) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer)'),
    (r'\.background\(if \(isOpen\) OrangeTint else Color\(0xFFF0F0F0\)\)', '.background(if (isOpen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)'),
    (r'\.background\(if \(isOpen\) OpenGreen\.copy\(alpha = 0\.10f\) else SurfaceBg\)', '.background(if (isOpen) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surfaceContainer)'),
    (r'\.background\(if \(selected\) TextPrimary else Color\.Transparent\)', '.background(if (selected) MaterialTheme.colorScheme.onSurface else Color.Transparent)'),
    (r'\.background\(if \(open\) MaterialTheme\.colorScheme\.primary else SurfaceBg\)', '.background(if (open) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer)'),
    (r'\.background\(Brush\.verticalGradient\(listOf\(SoftCream, Color\.White\)\)\)', '.background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.colorScheme.surface)))'),
    (r'\.background\(Brush\.verticalGradient\(listOf\(HeroPastel, Color\.White\)\)\)', '.background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surface)))'),
    (r'\.background\(Color\(0xFFF5F5F5\)\)', '.background(MaterialTheme.colorScheme.surfaceContainer)'),
    
    # Text colors
    (r'color = TextPrimary\b', 'color = MaterialTheme.colorScheme.onSurface'),
    (r'color = TextSecondary\b', 'color = MaterialTheme.colorScheme.onSurfaceVariant'),
    (r'color = Muted\b', 'color = MaterialTheme.colorScheme.onSurfaceVariant'),
    (r'color = SoftMuted\b', 'color = MaterialTheme.colorScheme.onSurfaceVariant'),
    (r'color = Color\.Black\b', 'color = MaterialTheme.colorScheme.onBackground'),
    (r'color = Color\.Gray\b', 'color = MaterialTheme.colorScheme.onSurfaceVariant'),
    (r'color = Color\.DarkGray\b', 'color = MaterialTheme.colorScheme.onSurfaceVariant'),
    (r'color = Color\.Red\b', 'color = MaterialTheme.colorScheme.error'),
    (r'color = StarGold\b', 'color = MaterialTheme.colorScheme.tertiary'),
    (r'color = WarningInk\b', 'color = MaterialTheme.colorScheme.secondary'),
    (r'color = OpenGreen\b', 'color = MaterialTheme.colorScheme.tertiary'),
    (r'color = ClosedRed\b', 'color = MaterialTheme.colorScheme.error'),
    (r'color = SoonOrange\b', 'color = MaterialTheme.colorScheme.secondary'),
    (r'color = SoftGreenText\b', 'color = MaterialTheme.colorScheme.tertiary'),
    (r'color = BorderGrey\b', 'color = MaterialTheme.colorScheme.outline'),
    (r'color = Ink\b', 'color = MaterialTheme.colorScheme.onSurface'),
    
    # Border colors
    (r'borderColor = BorderGrey', 'borderColor = MaterialTheme.colorScheme.outline'),
    
    # Switch colors
    (r'checkedTrackColor = OpenGreen', 'checkedTrackColor = MaterialTheme.colorScheme.tertiary'),
    (r'checkedTrackColor = Color\(0xFF2F9E44\)', 'checkedTrackColor = MaterialTheme.colorScheme.tertiary'),
    (r'uncheckedTrackColor = Color\(0xFFBDBDBD\)', 'uncheckedTrackColor = MaterialTheme.colorScheme.outline'),
    
    # Icon/Button tints
    (r'tint = TextPrimary\b', 'tint = MaterialTheme.colorScheme.onSurface'),
    (r'tint = TextSecondary\b', 'tint = MaterialTheme.colorScheme.onSurfaceVariant'),
    (r'tint = Muted\b', 'tint = MaterialTheme.colorScheme.onSurfaceVariant'),
    (r'tint = SoftMuted\b', 'tint = MaterialTheme.colorScheme.onSurfaceVariant'),
    (r'tint = StarGold\b', 'tint = MaterialTheme.colorScheme.tertiary'),
    (r'tint = WarningInk\b', 'tint = MaterialTheme.colorScheme.secondary'),
    
    # Status colors
    (r'val statusColor = when \(seller\.SellerOpenStatus\) \{(?:\n|.)*?\}', '# STATUS_COLOR_BLOCK'),
    
    # Specific OtpVerificationBottomSheet fixes
    (r'containerColor = Color\.White', 'containerColor = MaterialTheme.colorScheme.surface'),
    (r'color = Color\(0xFFFBE9E7\)', 'color = MaterialTheme.colorScheme.primaryContainer'),
    (r'color = Color\.Gray', 'color = MaterialTheme.colorScheme.onSurfaceVariant'),
    (r'tint = Color\.Red', 'tint = MaterialTheme.colorScheme.error'),
    (r'color = Color\(0xFFC62828\)', 'color = MaterialTheme.colorScheme.error'),
    (r'tint = Color\(0xFFBDBDBD\)', 'tint = MaterialTheme.colorScheme.outline'),
    (r'color = Color\(0xFFD0D0D0\)', 'color = MaterialTheme.colorScheme.onSurfaceVariant'),
]

# Special handling for Status color blocks in multiple files
status_color_block = """    val statusColor = when (seller.SellerOpenStatus) {
        SellerOpenStatus.OPEN -> MaterialTheme.colorScheme.tertiary
        SellerOpenStatus.OPENING_SOON -> MaterialTheme.colorScheme.primary
        SellerOpenStatus.CLOSED -> MaterialTheme.colorScheme.onSurfaceVariant
    }"""

for fpath in all_files:
    if not os.path.exists(fpath):
        print(f"NOT FOUND: {fpath}")
        continue
        
    try:
        with open(fpath, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        print(f"Error reading {fpath}: {e}")
        continue
    
    original = content
    
    # First add MaterialTheme import if not present
    if 'import androidx.compose.material3.MaterialTheme' not in content:
        # Find the last import line
        import_lines = []
        for line in content.split('\n'):
            if line.strip().startswith('import '):
                import_lines.append(line)
        if import_lines:
            last_import = import_lines[-1]
            content = content.replace(last_import, last_import + '\nimport androidx.compose.material3.MaterialTheme')
    
    # Handle status color block specially
    content = re.sub(
        r'val statusColor = when \(seller\.SellerOpenStatus\) \{[\s\S]*?\}',
        status_color_block,
        content
    )
    
    # Apply all replacements
    for pattern, repl in replacements:
        content = re.sub(pattern, repl, content)
    
    # Clean up the # STATUS_COLOR_BLOCK placeholder
    content = content.replace('# STATUS_COLOR_BLOCK', status_color_block)
    
    if content != original:
        try:
            with open(fpath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Updated: {fpath}")
        except Exception as e:
            print(f"Error writing {fpath}: {e}")

print("Done!")