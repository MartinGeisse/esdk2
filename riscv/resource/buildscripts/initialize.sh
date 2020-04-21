ssh martin@ise rm -rf auto-ise
ssh martin@ise mkdir auto-ise
scp remote-scripts/build.txt martin@ise:auto-ise/build.sh
scp remote-scripts/clean.txt martin@ise:auto-ise/clean.sh
scp remote-scripts/upload.txt martin@ise:auto-ise/upload.sh
scp remote-scripts/upload-prom.txt martin@ise:auto-ise/upload-prom.sh
scp remote-scripts/impact.txt martin@ise:auto-ise/impact.txt
scp remote-scripts/impact-prom.txt martin@ise:auto-ise/impact-prom.txt
scp remote-scripts/load_firmware.txt martin@ise:auto-ise/load_firmware.sh
ssh martin@ise chmod u+x 'auto-ise/*'
ssh martin@ise ls -al auto-ise
