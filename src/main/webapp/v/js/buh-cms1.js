var setPageKey = function($scope){
	console.log(window.location);
	var locationString = ""+window.location;
	var locationSplit = locationString.split("/");
	var lastURN = locationSplit[locationSplit.length - 1]
	var isHtml = lastURN.indexOf(".html") > -1
	if(isHtml){
		$scope.pageKey = window.location.search.substring(1);
	}else{
		var hasParam = lastURN.indexOf("?") > -1
		if(hasParam){
			$scope.pageKey = lastURN.split("?")[0];
		}else{
			var hasInnerAnchor = lastURN.indexOf("#") > -1
			if(hasInnerAnchor){
				$scope.pageKey = lastURN.split("#")[0];
			}else{
				$scope.pageKey = lastURN;
			}
		}
	}
	console.log($scope.pageKey);
}

var initFunction = function($scope, $http){
	setPageKey($scope);
	$http.get("/v/readContent").success(function(response) {
		$scope.jsonFromRam = response;
		$scope.pageObject = response.pages[$scope.pageKey];
	});

	$scope.pagesKeys = function(object){
		if(object)
			return Object.keys(object);
	}

	$http.get("/read_user").success(function(data) {
		$scope.user = data;
		if($scope.user.authorities)
			$scope.role = $scope.user.authorities[0].authority
			.split("_")[1];
	});
	
	$scope.saveJsonToFile = function(){
		$http.post('/saveCommonContent', $scope.jsonFromRam).
		then(function(response) {
			console.log("---saveCommonContent------success--------");
		}, function(response) {
			console.log("---saveCommonContent-------erros-------");
		});
	}
}

angular.module("sah1App", ['textAngular'])
.controller("sah1Ctrl", function initController($scope, $http) {
	initFunction($scope, $http);
});

angular.module("sitemap1App", ['textAngular'])
.controller("sitemap1Ctrl", function initController($scope, $http) {
	console.log("-----sitemap1Ctrl-----------");
	initFunction($scope, $http);

	$scope.verifyPagesKeysUniqueObject = {};
	$scope.hasNewPage = false;

	$scope.initVerifyPagesKeysUnique = function(pageKey){
		$scope.verifyPagesKeysUniqueObject[pageKey] = pageKey;
	}

	$scope.changePageKey = function(){
		var changed = false;
		console.log($scope.hasNewPage);
		console.log(changed);
		console.log($scope.verifyPagesKeysUniqueObject);
		angular.forEach(Object.keys($scope.verifyPagesKeysUniqueObject), function (key) {
			if( key != $scope.verifyPagesKeysUniqueObject[key]){
				changed = true;
				var newKey = $scope.verifyPagesKeysUniqueObject[key];
				$scope.verifyPagesKeysUniqueObject[ newKey ]
					= $scope.verifyPagesKeysUniqueObject[ key ];
				delete $scope.verifyPagesKeysUniqueObject[ key ];
				$scope.jsonFromRam.pages[ newKey ]
					= $scope.jsonFromRam.pages[ key ];
				delete $scope.jsonFromRam.pages[ key ];
			}
		});
		console.log(changed);
		if(changed || $scope.hasNewPage){
			console.log("---saveJsonToFile-----------");
			$scope.saveJsonToFile();
		}
	}

	$scope.addPage = function(){
		var lastPage = 0;
		var maxPage = 0;
		if($scope.jsonFromRam)
			angular.forEach(Object.keys($scope.jsonFromRam.pages).sort(), function (item, i) {
				if(item.indexOf("page")==0){
					lastPage = item.split("page")[1];
					maxPage = Math.max(maxPage, lastPage);
				}
			});
		console.log(lastPage+"/"+maxPage);
		var page = "page" + (maxPage/1 + 1);
		$scope.jsonFromRam.pages[page] = {"title" : page, "fileType" : "textHtml1", "html" : "page text"};
		console.log($scope.jsonFromRam);
		$scope.hasNewPage = true;
	}

	$scope.verifyPagesKeysUnique = function(pageKey, newPageKey){
		if(newPageKey!=pageKey){
			if($scope.verifyPagesKeysUniqueObject[newPageKey]){
				var correct = newPageKey.substring(0, newPageKey.length - 1);
				$scope.verifyPagesKeysUniqueObject[pageKey] = correct;
			}
		}
	}

});
