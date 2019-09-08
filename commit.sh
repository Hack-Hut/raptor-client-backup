git add .
git commit
git push origin master
curl -X POST -u luke:password http://127.0.0.1:8090/job/raptor-client-build/build
