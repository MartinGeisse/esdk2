ssh martin@ise ./auto-ise/clean.sh
scp -r . martin@ise:./auto-ise/src
ssh martin@ise ./auto-ise/build.sh environment.sh
