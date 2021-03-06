cap.controller("RepositoryViewContextController", function ($controller, $filter, $location, $routeParams, $scope, $timeout, $q, RepositoryViewRepo, SchemaRepo, FixityReport) {

  angular.extend(this, $controller('AbstractAppController', {
    $scope: $scope
  }));

  $scope.repositoryViewForm = {};

  $scope.submitClicked = false;

  $scope.theaterMode = false;

  $scope.messagingEnabled = appConfig.messagingEnabled;

  $scope.setOrToggleTheaterMode = function (mode) {
    $scope.theaterMode = mode ? mode : !$scope.theaterMode;
  };

  RepositoryViewRepo.ready().then(function () {
    $scope.repositoryView = RepositoryViewRepo.findByName(decodeURI($routeParams.irName));

    var shortenContextUri = function (contextUri) {
      return $scope.repositoryView.rootUri ? $filter('shortenUri')(contextUri, $scope.repositoryView.rootUri) : contextUri;
    };

    if (!$scope.repositoryView) $location.path("/error/404");

    if ($routeParams.context !== undefined) {
      $scope.repositoryView.contextUri = decodeURI($routeParams.context);
    } else {
      $location.search("context", $scope.repositoryView.contextUri);
    }

    $scope.context = $scope.repositoryView.loadContext($scope.repositoryView.contextUri);

    $scope.context.schemas = SchemaRepo.getAll();

    $scope.context.propertiesCollapsed = {};

    $scope.context.metadataCollapsed = {};

    $scope.context.metadataCollapsedByNamespace = {};

    $scope.createContainer = function (form) {
      $scope.submitClicked = true;
      var subject = $scope.context.uri;
      var triples = [];
      angular.forEach(form.entries, function (entry) {
        if (entry.property && entry.value) {
          triples.push({
            subject: subject,
            predicate: entry.property.uri,
            object: entry.value
          });
        }
      });

      $scope.context.createContainer(triples).then(function () {
        $scope.closeModal();
        $scope.submitClicked = false;
      });
    };

    $scope.updateMetadatum = function (triple, value) {
      var defer = $q.defer();
      var promise = defer.promise;
      if (value && (value.length === 0 || value !== "\"\"")) {
        promise = $scope.context.updateMetadatum(triple, value);
      } else {
        defer.reject("Update Rejected: Value was empty.");
      }
      return promise;
    };

    $scope.advancedUpdate = function (sparql) {
      $scope.submitClicked = true;
      $scope.context.advancedUpdate(sparql).then(function () {
        $scope.closeModal();
        $scope.submitClicked = false;
      });
    };

    $scope.uploadResource = function (file) {
      $scope.submitClicked = true;
      $scope.context.createResource(file).then(function () {
        $scope.resetUploadResource();
        $scope.submitClicked = false;
      });
    };

    $scope.resetCreateContainer = function () {
      $scope.repositoryViewForm.createContainer = {
        name: ""
      };
      $scope.closeModal();
    };

    $scope.resetUploadResource = function () {
      if ($scope.repositoryViewForm.uploadResource) {
        delete $scope.repositoryViewForm.uploadResource.file;
      }
      $scope.closeModal();
    };

    $scope.resetAdvancedUpdate = function () {
      $scope.repositoryViewForm.advancedUpdate = $scope.context.getQueryHelp();
      $scope.closeModal();
    };

    $scope.resetAddMetadataModal = function () {
      $scope.repositoryViewForm.addMetadata.$setPristine();
      $scope.repositoryViewForm.addMetadata.entries.length = 0;
      $scope.repositoryViewForm.addMetadata.entries.push({});
      $scope.closeModal();
    };

    $scope.addMetadata = function (form) {
      $scope.submitClicked = true;

      var triples = [];
      angular.forEach(form.entries, function (entry) {
        triples.push({
          subject: $scope.context.uri,
          predicate: entry.property.uri,
          object: $filter("escapeLiteral")(entry.value)
        });
      });

      $scope.context.createMetadata(triples).then(function () {
        $scope.resetAddMetadataModal();
        $scope.submitClicked = false;
      });

    };

    $scope.cancelDeleteRepositoryViewContext = function () {
      $scope.repositoryViewContextToDelete = {};
      $scope.closeModal();
    };

    $scope.deleteRepositoryViewContext = function () {
      $scope.submitClicked = true;

      $scope.closeModal().then(function () {
        var repositoryView = $scope.context.repositoryView;
        var currentTriple = $scope.context.triple;
        var isResource = $scope.context.resource;

        var deleteContext = isResource ? $scope.context.removeResources : $scope.context.removeContainers;

        var parentContextUri = shortenContextUri($scope.context.parent.object);

        deleteContext([currentTriple]).then(function () {
          $scope.context = repositoryView.loadContext(parentContextUri, true);
          $scope.submitClicked = false;
        });
      });
    };

    $scope.viewVersion = function (version) {
      $scope.closeModal().then(function () {
        var repositoryView = $scope.context.repositoryView;
        var versionContextUri = shortenContextUri(version.triple.object);
        $scope.context = repositoryView.loadContext(versionContextUri, true);
      });
    };

    $scope.revertVersion = function () {
      $scope.submitClicked = true;

      $scope.closeModal().then(function () {
        var repositoryView = $scope.context.repositoryView;
        var currentContext = $scope.context;
        var currentContextUri = shortenContextUri(currentContext.uri);

        var parentContextUri = shortenContextUri($scope.context.parent.object);

        repositoryView.removeCachedContext(currentContextUri);

        $scope.context.revertVersion(currentContext).then(function () {
          $scope.context = repositoryView.loadContext(parentContextUri, true);
          $scope.submitClicked = false;
        });
      });
    };

    $scope.deleteVersion = function () {
      $scope.submitClicked = true;

      $scope.closeModal().then(function () {
        var repositoryView = $scope.context.repositoryView;
        var currentContext = $scope.context;
        var currentContextUri = shortenContextUri(currentContext.uri);

        var parentContextUri = shortenContextUri($scope.context.parent.object);

        repositoryView.removeCachedContext(currentContextUri);

        $scope.context.deleteVersion(currentContext).then(function () {
          $scope.context = repositoryView.loadContext(parentContextUri, true);
          $scope.submitClicked = false;
        });
      });
    };

    $scope.refreshContext = function () {
      $scope.context.refreshContext();
    };

    $scope.openFixity = function (uriOfContextToCheck) {
      $scope.fixityReport = new FixityReport({
        repositoryView: $scope.context.repositoryView,
        contextUri: uriOfContextToCheck
      });
      $scope.openModal('#fixityModalButton');
    };

    $scope.cancelFixity = function () {
      $scope.fixityReport = {};
      $scope.closeModal();
    };

    $scope.startTransaction = function () {
      $scope.context.repositoryView.startTransaction();
    };

    $scope.commitTransaction = function () {
      $scope.submitClicked = true;
      $scope.context.repositoryView.commitTransaction().then(function () {
        $scope.closeModal();
        $scope.submitClicked = false;
      });
    };

    $scope.rollbackTransaction = function () {
      $scope.submitClicked = true;
      $scope.context.repositoryView.rollbackTransaction().then(function () {
        $scope.closeModal();
        $scope.submitClicked = false;
      });
    };

    $scope.copyToClipboard = function (text, target) {
      var textArea = document.createElement("textarea");
      textArea.value = text;
      document.body.appendChild(textArea);
      textArea.select();
      try {
        document.execCommand('copy');
        $scope[target + "Copied"] = true;
      } catch (e) {
        console.log("browser doesn't support copying to clipboard");
      } finally {
        document.body.removeChild(textArea);
        $timeout(function () {
          $scope[target + "Copied"] = false;
        }, 1500);
      }
    };

    $scope.srcFromFile = function (file) {
      return URL.createObjectURL(file);
    };

    $scope.getContentType = function () {
      var contentType = null;
      var backupContentType = null;
      var typeMap = { "jpg": "image/jpeg", "png": "image/png", "pdf": "application/pdf" };

      for (var i in $scope.context.properties) {
        var triple = $scope.context.properties[i];
        if (triple.predicate.indexOf("#hasMimeType") !== -1) {
          contentType = $filter("escapeLiteral")(triple.object);
          break;
        }

        if (triple.predicate.indexOf("#filename") !== -1) {
          var temp = triple.object.split(".");
          temp = temp[temp.length - 1];
          backupContentType = temp.substring(0, temp.length - 1);
          if (typeMap[backupContentType] !== undefined) {
            backupContentType = typeMap[backupContentType];
          }
        }
      }

      if (!contentType && backupContentType) {
        contentType = backupContentType;
      }
      return contentType;
    };

    $scope.canPreview = function (fileType) {
      var previewable = ['image/png', 'image/jpeg', 'image/gif', 'image/bmp'];
      return (previewable.indexOf(fileType) !== -1);
    };

    $scope.getIIIFUrl = function () {
      if (typeof appConfig.iiifServiceUrl !== 'undefined') {
        var contextProperties = [];
        var iiifUri = null;

        if ($scope.context.resource && $scope.context.hasParent) {
          var parentContext = $scope.context.repositoryView.getContext(shortenContextUri($scope.context.parent.object));
          if (parentContext.hasParent) {
            var grandParentContext = $scope.context.repositoryView.getContext(shortenContextUri(parentContext.parent.object));
            contextProperties = grandParentContext.properties;
            iiifUri = grandParentContext.uri;
          }
        } else {
          contextProperties = $scope.context.properties;
          iiifUri = $scope.context.uri;
        }

        if (iiifUri && contextProperties) {
          var hasManifest = false;
          var hasFile = false;
          var isPCDM = false;
          for (var i in contextProperties) {
            var triple = contextProperties[i];
            if (!hasFile) {
              if (triple.predicate.indexOf("#hasFile") !== -1) {
                hasFile = true;
              }
            }
            if (!isPCDM) {
              if (triple.predicate.indexOf("#type") !== -1) {
                if (triple.object === "http://pcdm.org/models#Object") {
                  isPCDM = true;
                }
              }
            }
            if (isPCDM && hasFile) {
              hasManifest = true;
              break;
            }
          }
          if (hasManifest) {
            return appConfig.iiifServiceUrl + $scope.repositoryView.type.toLowerCase() + "/presentation/" + iiifUri.replace($scope.repositoryView.rootUri, "");
          }
        }
      }
      return null;
    };

    $scope.resetCreateContainer();
    $scope.resetUploadResource();

  });

  $scope.lengthenContextUri = function (contextUri) {
    return $scope.context.repositoryView.rootUri ? $filter('lengthenUri')(contextUri, $scope.context.repositoryView.rootUri) : contextUri;
  };

});
