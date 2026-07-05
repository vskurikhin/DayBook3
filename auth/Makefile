include .env

PROJECTNAME=$(shell basename "$(PWD)")

# Go related variables.
GOBASE=$(shell pwd)
GOPATH="$(GOBASE)/vendor:$(GOBASE)"
CMD_SERVER=cmd/server
GOBIN=$(GOBASE)/$(CMD_SERVER)
GOFILES=$(wildcard *.go)

# Redirect error output to a file, so we can show it in development mode.
STDERR=/tmp/.$(PROJECTNAME)-server-stderr.txt

# PID file will keep the process id of the auth
PID_AUTH=/tmp/.$(PROJECTNAME)-server.pid

RANDOM=$(shell date +%s)
RND1=$(shell echo "("$RANDOM" % 1024) + 63490" | bc)
AUTH_PORT=$(RND1)
RND2=$(shell echo "("$RANDOM" % 1024) + 64514" | bc)
ACCRUAL_PORT=$(RND2)
ADDRESS=localhost:$(AUTH_PORT)
TEMP_FILE=$(shell mktemp)
DOCS_DIR=./docs
DOCS_GO=$(DOCS_DIR)/swagger.yaml
MAIN_GO=./$(CMD_SERVER)/main.go

# Make is verbose in Linux. Make it silent.
MAKEFLAGS += --silent

## run: Compile and run server and agent
run: go-compile

## coverage: Calculate coverage
coverage: go-coverage

## start: Start in development mode. Auto-starts when code changes.
start: start-auth

## stop: Stop development mode. AUTH
stop: stop-auth

start-auth: stop-auth
	@echo "  >  $(PROJECTNAME) is available at $(ADDRESS)"
	@-$(GOBIN)/$(PROJECTNAME)-server run --address "$(ADDRESS)" & echo $$! > $(PID_AUTH)
	@cat $(PID_AUTH) | sed "/^/s/^/  \>  PID: /"

stop-auth:
	@-touch $(PID_AUTH)
	@-kill `cat $(PID_AUTH)` 2> /dev/null || true
	@-rm $(PID_AUTH)

restart-auth: stop-auth start-auth

build: go-build-auth

## clean: Clean build files. Runs `go clean` internally.
clean: go-clean
	@-rm $(GOBIN)/$(PROJECTNAME)-server

## compile: Compile the binary.
go-compile: go-build-auth

go-build-auth:
	@echo "  >  Building gopher $(PROJECTNAME)-server binary..."
	@GOPATH=$(GOPATH) GOBIN=$(GOBIN) cd ./cmd/server && go build -o $(GOBIN)/$(PROJECTNAME)-server $(GOFILES)

go-generate:
	@echo "  >  Generating dependency files..."
	@GOPATH=$(GOPATH) GOBIN=$(GOBIN) go generate $(generate)

go-generate-wire:
	@echo "  >  Generating files by Wire...."
	@GOPATH=$(GOPATH) GOBIN=$(GOBIN) cd ./cmd/server/wire && ( go run -mod=mod github.com/google/wire/cmd/wire; cd - )

# 1. Generate the list of packages to include, excluding 'mocks'
# go list ./... lists all packages; grep -v excludes lines containing 'mocks'
go-coverage:
	CVPKG=$(go list ./... | grep -v '/mocks' | tr '\n' ',')
	# 2. Run tests with coverage analysis applied only to the specified packages
	# @GOPATH=$(GOPATH) GOBIN=$(GOBIN) go test -coverpkg=$(CVPKG) -coverprofile=coverage.out ./...
	@GOPATH=$(GOPATH) GOBIN=$(GOBIN) go test -coverprofile=coverage.out ./...
	# 3. (Optional) Generate the HTML report
	@GOPATH=$(GOPATH) GOBIN=$(GOBIN) go tool cover -html=coverage.out -o docs/coverage.html

go-get:
	@echo "  >  Checking if there is any missing dependencies..."
	@GOPATH=$(GOPATH) GOBIN=$(GOBIN) go get $(get)

.PHONY: go-update-deps
go-update-deps:
	@echo ">> updating Go dependencies"
	@for m in $$(go list -mod=readonly -m -f '{{ if and (not .Indirect) (not .Main)}}{{.Path}}{{end}}' all); do \
		go get $$m; \
	done
	go mod tidy
ifneq (,$(wildcard vendor))
	go mod vendor
endif

go-install:
	@GOPATH=$(GOPATH) GOBIN=$(GOBIN) go install $(GOFILES)

go-swag:
	swag init --parseInternal --st -ot yaml -g $(MAIN_GO) --output $(DOCS_DIR)
	gsed -i 's/host: localhost:8089/host: $(ADDRESS)/' $(DOCS_GO)
	cd docs ; redocly build-docs ./swagger.yaml ; cd -

go-clean:
	@echo "  >  Cleaning build cache"
	@GOPATH=$(GOPATH) GOBIN=$(GOBIN) go clean

goose-status:
	@echo "  >  Goose migrations status"
	cd ./cmd/server/cmd/migrations/ && goose postgres "postgres://dbuser:password@localhost:5432/db?application_name=auth&search_path=auth" status

sqlc-generate:
	@echo "  >  Generating files by sqlc...."
	sqlc generate -f sqlc_role.yaml
	sqlc generate -f sqlc_users.yaml
	sqlc generate -f sqlc_session.yaml

sqlc-user-view-table:
	@echo "  >  Generating schema_user_view.sql...."
	@echo "  >  Cleaning build cache"
	echo "CREATE TABLE user_view_table AS SELECT * FROM user_view WHERE user_name IS NULL" | psql -h localhost -U dbuser db
	pg_dump --schema-only --no-comments --no-policies --no-privileges --no-owner --table=user_view_table -h localhost -U dbuser db > internal/server/sqlc/schema_user_view.sql
	echo "DROP TABLE IF EXISTS user_view_table" | psql -h localhost -U dbuser db

.PHONY: help
all: help
help: Makefile
	@echo
	@echo " Choose a command run in "$(PROJECTNAME)":"
	@echo
	@sed -n 's/^##//p' $< | column -t -s ':' |  sed -e 's/^/ /'
	@echo
