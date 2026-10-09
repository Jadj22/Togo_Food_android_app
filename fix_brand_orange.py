#!/usr/bin/env python3
import os
import re
import sys

root = r"C:\Users\Jadj\AndroidStudioProjects\TogoFood\app\src\main"

# Patterns to replace
replacements = [
    # Import BrandOrange -> MaterialTheme
    (r'import com\.example\.togofood\.ui\.components\.BrandOrange', 
     'import androidx.compose.material3.MaterialTheme'),
    
    # BrandOrange -> MaterialTheme.colorScheme.primary
    (r'\bBrandOrange\b', 
     'MaterialTheme.colorScheme.primary'),
    
    # BrandOrangeLight -> MaterialTheme.colorScheme.primaryContainer
    (r'\bBrandOrangeLight\b', 
     'MaterialTheme.colorScheme.primaryContainer'),
]

# Also need to add Color import where needed for Color.White, Color.Black, etc.
# But those are standard androidx.compose.ui.graphics.Color

# Color.Transparent is from androidx.compose.ui.graphics.Color
# Color.Unspecified is from androidx.compose.ui.graphics.Color

for dirpath, dirnames, filenames in os.walk(root):
    for fname in filenames:
        if not fname.endswith('.kt'):
            continue
        fpath = os.path.join(dirpath, fname)
        try:
            with open(fpath, 'r', encoding='utf-8') as f:
                content = f.read()
        except Exception as e:
            print(f"Error reading {fpath}: {e}")
            continue
        
        original = content
        for pattern, repl in replacements:
            content = re.sub(pattern, repl, content)
        
        if content != original:
            try:
                with open(fpath, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"Updated: {fpath}")
            except Exception as e:
                print(f"Error writing {fpath}: {e}")

print("Done!")