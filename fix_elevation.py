import re
import glob

def clean_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Remove all added lines
    content = re.sub(r',\s*elevation = CardDefaults\.cardElevation\(defaultElevation = [0-9]+\.dp\)', '', content)
    # Then I will add ONE safely where it belongs, but maybe elevation isn't strictly necessary if tonalElevation looks good.
    # Actually, in modern M3, tonalElevation or simply the containerColor is enough.
    
    with open(filepath, 'w') as f:
        f.write(content)

for f in glob.glob('app/src/main/java/com/example/ui/screens/*.kt'):
    clean_file(f)
