CWD="$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
make -C $CWD clean
make -C $CWD -j 12
echo "Dev null test" > /dev/null
