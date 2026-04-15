#!/usr/bin/env bash
set -euo pipefail

"$(dirname "$0")/extract_sources.sh"
mkdir -p tests/saidas

run_case() {
  local input_file="$1"
  local output_file="$2"
  if java LexicalCompiler.java "$input_file" > "$output_file" 2>&1; then
    :
  else
    :
  fi
}

run_case tests/originais/teste1.txt tests/saidas/teste1_original.txt
run_case tests/corrigidos/teste1_c.txt tests/saidas/teste1_c.txt
run_case tests/originais/teste2.txt tests/saidas/teste2_original.txt
run_case tests/corrigidos/teste2_c.txt tests/saidas/teste2_c.txt
run_case tests/originais/teste3.txt tests/saidas/teste3_original.txt
run_case tests/corrigidos/teste3_c.txt tests/saidas/teste3_c.txt
run_case tests/originais/teste4.txt tests/saidas/teste4_original.txt
run_case tests/corrigidos/teste4_c.txt tests/saidas/teste4_c.txt
run_case tests/originais/teste5.txt tests/saidas/teste5_original.txt
run_case tests/corrigidos/teste5_c.txt tests/saidas/teste5_c.txt
run_case tests/corrigidos/teste6_c.txt tests/saidas/teste6_c.txt