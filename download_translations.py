import re
import os
import time
import urllib.request
from html.parser import HTMLParser

print("Fetching translation list from tanzil.net...")
try:
    html = urllib.request.urlopen("http://tanzil.net/trans/").read().decode("utf-8")
except Exception as e:
    print(f"Error fetching page: {e}")
    exit(1)

# استخراج شناسه‌ها از لینک‌های دانلود
ids = re.findall(r'href="/trans/([^"/]+)" title="Download"', html)
ids = list(dict.fromkeys(ids))

print(f"Found {len(ids)} translations")

os.makedirs("app/src/main/assets/translations", exist_ok=True)

for tid in ids:
    out_path = os.path.join("app/src/main/assets/translations", f"{tid}.xml")
    if os.path.exists(out_path):
        print(f"skip  {tid} (already exists)")
        continue
    url = f"http://tanzil.net/trans/?transID={tid}&type=xml"
    print(f"get   {tid} ...", end=" ", flush=True)
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=30) as resp:
            data = resp.read()
        with open(out_path, "wb") as f:
            f.write(data)
        print(f"OK ({len(data)} bytes)")
    except Exception as e:
        print(f"FAIL: {e}")
    time.sleep(0.3)

print("\nDone downloading translations.")
