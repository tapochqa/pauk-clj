NI_ARGS = \
	--initialize-at-build-time \
	--report-unsupported-elements-at-runtime \
	--no-fallback \
	-jar target/default+uberjar/pauk-clj-0.1.0-standalone.jar \
	-H:IncludeResources='^token$$' \
	--enable-http \
	--enable-https \
	-H:+PrintClassInitialization \
	-H:ReflectionConfigurationFiles=reflection-config.json \
	-H:+ReportExceptionStackTraces \
	-H:Log=registerResource \
	-H:Name=./builds/pauk-clj-

PWD = $(shell pwd)

cp:
	git add .
	@read -p "Commit message: " m; \
	git commit -m "$$m"
	git push --all gh

build-native-macos:
	sudo native-image ${NI_ARGS}macos

build-native-linux:
	docker run -it --rm -v ${PWD}:/build -w /build ghcr.io/graalvm/native-image:22.2.0 ${NI_ARGS}$linux