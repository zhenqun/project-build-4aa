
var userListView = {
    api: {
        query: '/manage/internet/internetUserQuery',
        querySystem: '/manage/internet/internetApplicationQuery',
        status:'/manage/internet/modifyInternetUserStatus/',
        dist: '/manage/internet/allotInternetUserApp',
        reset:'/manage/resetInternetUserPwd/'
    },
    events: {
        'click a[cmd=page]': 'goTopage',       
        'click button[cmd=reserveId]': 'reserveId',
        'click button[cmd=dist]': 'dist',
        'click button[cmd=changeStatus]':'changeStatus',
        'click button[cmd=resetPassword]':'resetPassword'
    },
    element: '#user_list_html',
    use: ['queryView', 'templateAutoRender'],
    initialize: function () {
        this.template = Handlebars.compile($('#user_list_tpl').html());
        this.query(); 
    },
    render: function () {
        this.$el.html(this.template(this.model));
    },
    query: function () {
        var _this = this;
        var pageNo = _this.model.CurrentPage;
        var pageSize = _this.model.PageSize;
        // pageSize=3;
        var param = $('#queryForm').serializeJSON();
        _.extend(param, { pageSize: pageSize, pageNo: pageNo });
        param.applicationIds = [];            
        var promises = [$.postJSON(this.api.querySystem),
        $.postJSON(this.api.query, param)];
        Promise.all(promises)
        .then(function(data){
            var count = data[1].count
            var feedbackData = data[1].data;
            for (i in feedbackData) {
                // var content = feedbackData[i].feedbackContent;
                feedbackData[i].index = Number(i) + 1;
                feedbackData[i].registerDate = new Date(feedbackData[i].registerDate);
                feedbackData[i].lastLoginDate = new Date(feedbackData[i].lastLoginDate);
                if (feedbackData[i].status == 0) {
                    feedbackData[i].isAbled = true;
                }
                if (feedbackData[i].status == 1) {
                    feedbackData[i].disabled = true;
                }
                if (feedbackData[i].identity == 0) {
                    feedbackData[i].isCivilian = true;
                };

            };
            _this.dataList = feedbackData;
            dataSource = feedbackData;
            _.extend(_this.model, { data1: dataSource });
            //////
             var systemData = data[0];           
            _.extend(_this.model, { systemData: systemData,pageNo:pageNo,pageSize:pageSize,count:count }); 
            _this.render(); 
        });




    },
    resetPassword:function(e){
        var id = $(e.target).attr('userId');     
        $.getJSON(this.api.reset + id).then(function (dataSource) {
            if (dataSource) {
                toastr.success("操作成功");
            }
            else {
                toastr.error("操作失败");                
            }
        })
    },
    querySystem: function () {
        var _this = this;      
        
    },
    changeStatus: function (e) {
        var _this = this;
        var id = $(e.target).attr('userId');
        var status = Number(!Number($(e.target).attr('status'))).toString();
        $.getJSON(this.api.status + id + '/' + status).then(function (dataSource) {
            if (dataSource) {
                toastr.success("操作成功");
                _this.query();
            }
            else {
                toastr.error("操作失败");
            }
        })
    },
    reserveId: function (e) {
        $("input[name='system']").each(function(idx,ele){
            $(ele).prop('checked',false);
        });
        this.userId = $(e.target).attr('userId');
        console.log(this.userId);
        var userId = '';
        var userSysIds = [];
        if (this.userId) {
            userId = this.userId;
            userSysIds = this.dataList.find(function (x) { return x.userId == userId }).applicationIds.split(',');
        };
         if (userSysIds.length > 0) {
             userSysIds.forEach(function(id){
                $("tr[name='systemTr']").each(function(idx,ele){
                    if( $(ele).attr('systemId') == id) {
                        $(ele).find("input[name='system']").prop('checked',true);
                    }
                })
             })
        }
    },    
    dist: function () {
        var applications = [];
        var _this = this;
        $("tr[name='systemTr']").each(function(idx,ele){
            if($(ele).find("input[name='system']").prop('checked')){
                 applications.push({
                     applicationId:$(ele).attr('systemId'),
                     orgId:''
                });
            }           
        })
       
         _this.render();
        $.postJSON(this.api.dist, { userId: this.userId, applications: applications }).then(function (dataSource) {
            // _.extend(_this.model, {changeStatusResult:dataSource});            	
            if (dataSource) {
                 toastr.success("操作成功");
                _this.query();
               
            }
            else {
                toastr.error("操作失败");
            }
            $('#modal-3').modal('hide');
            $('.modal-backdrop').addClass('hide');
        })
    },
    goTopage: function (e) {
        e.preventDefault();
        this.model.CurrentPage = $(e.target).attr('arg');
        this.query();
    }, queryStart: function () {
        this.model.CurrentPage = 1;
        this.query();
    }
};
     