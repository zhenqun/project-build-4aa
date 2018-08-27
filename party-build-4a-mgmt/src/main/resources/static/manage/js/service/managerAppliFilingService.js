/**
 * Created by Administrator on 2017/10/25.
 */
(function(angular){
    angular.module('mgnt')
        .service('managerAppliFilingService',function($http){
            return{
                /**
                 * 管理员申请列表查询
                 * @param params
                 * @returns {*}
                 */
                query: function(params){
                    return $http.post('/assistSecurity/auditAdminList', params);
                },

                /**
                 * 驳回
                 * @param params
                 * @returns {*}
                 */
                reject: function(params){
                    return $http.post('/assistSecurity/vetoApplyUser', params);
                },

                /**
                 * 校验
                 * @param params
                 * @returns {*}
                 */
                check: function(params){
                    return $http.post('/assistSecurity/checkApplyUser', params);
                },

                /**
                 * 通过
                 * @param params
                 * @returns {*}
                 */
                pass: function (params) {
                    return $http.post('/assistSecurity/passApplyUser', params);
                }


            }
        });
})(angular);