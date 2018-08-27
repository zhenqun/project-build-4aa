
		//绑通知公告列表
        var NoticeListView = {
            api: {
                query: '/bintang/getNoticeList',
            },
            events: {
            	'click a[cmd=detailed]': 'lookNotice',
            	'click a[cmd=page]': 'goTopage'
            },
            element: '#notice_list_html',
            use: ['queryView', 'templateAutoRender'],
            initialize: function () {
                this.template = Handlebars.compile($('#notice_list_tpl').html());
                this.query();
            },
            render: function () {
//            	debugger
            	//this.autoRender();
                this.$el.html(this.template(this.model));
            },
            query: function () {
            	
                var _this = this;
                var pageNo = this.model.CurrentPage;
                var pageSize = this.model.PageSize;
                //var param = $('#pk_select_form').serializeJSON();
                $.postJSON(this.api.query, {pageSize: pageSize, pageNo: pageNo }).then(function (data) {
                    _.extend(_this.model, data);
                    _this.render();
                })
            },
            lookNotice: function (e) {
                e.preventDefault();
                var id = $(e.target).attr('arg'); 
                location.href = "/noticedetailed/" + id;
            },
            goTopage: function (e) {
                e.preventDefault();
                this.model.CurrentPage = $(e.target).attr('arg');
                this.query();
            }
        };
        
        //绑定通知公告详细 
        var NoticeView = {
            api: {
                query: '/bintang/announcementDetail',
            },

            events: {},
            element: '#notice_html',
            use: ['queryView'],
            initialize: function () { 
                this.template = Handlebars.compile($('#notice_tpl').html());
                this.query();
            },
            render: function () {
                this.$el.html(this.template(this.model));
            },
            query: function () {
                var _this = this;
                _.extend(_this.model, data);
                _this.render();
            }

        };
        
        