#!/usr/bin/env bash
set -e

echo $1
echo $2

# prepare
mkdir -p "$2"
# run
find "$1" -maxdepth 1 -type f -name "*.adoc" -print0 |
    while IFS= read -r -d '' line; do
      manual="${line%.*}"
      echo "Processing $manual ..."
      # Convert asciidoc to docbook using asciidoctor
      asciidoctor -b docbook "${manual}.adoc"
      # foo.xml will be output into the same directory as foo.adoc
      iconv -t utf-8 "${manual}.xml" | pandoc -f docbook -t gfm | iconv -f utf-8 > "${manual}.md"
      # Copy to the target
      cp "${manual}.md" "$2"/
    done
