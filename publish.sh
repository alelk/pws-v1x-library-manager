export NEXT_RELEASE_VERSION=$(cat .nextRelease.txt)
if [[ $NEXT_RELEASE_VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Publishing $NEXT_RELEASE_VERSION as release"
  echo $NEXT_RELEASE_VERSION > app.version
elif [[ $NEXT_RELEASE_VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+-.+$ ]]; then
  echo "Publishing $NEXT_RELEASE_VERSION as snapshot"
  echo "${NEXT_RELEASE_VERSION%-*}-SNAPSHOT" > app.version
  sbt publish
else
  echo "No release published"
fi