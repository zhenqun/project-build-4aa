(function (angular) {
    angular.module('mgnt')
        .directive('orgVerify', function ($http, $q) {
            'ngInject';

            return {
                restrict: 'A',
                require: 'ngModel',
                link: function (scope, attr, ele, ngModel) {
                    // var chk = scope.client.chk;
                    var clientId = ele.clientid;
                    var type;
                    switch (ele.roleType) {
                        case 'work': type = '0';break;
                        case 'security': type = '1';break;
                        case 'audit': type = '2';break;
                    }
                    ngModel.$asyncValidators.errOrg = function (mval, vval) {
                        if (mval != ''&&!ngModel.$pristine) {
                            return $http.post('/manage/business/checkRole', {
                                roleManageId: mval,
                                clientId: clientId,
                                type: type
                            })
                                .then(function (result) {
                                    if (result.data) {
                                        return true
                                    }
                                    else {
                                        return $q.reject('invalid orgId')
                                    }
                                });
                        }
                        return $q.resolve('orgId is empty');
                    };
                    // ngModel.$validators.nullOrg = function (mval, vval) {
                    //     if (chk) {
                    //         return mval != ''
                    //     }
                    //     else {
                    //         return true
                    //     }
                    // }
                }
            };
        });
})(angular);
