
		//已完成课程
        var feedbackListView = {
            api: {
                query: '/user/getfeedbacklist',
            },
            events: {            	            	
            	'click a[cmd=page]': 'goTopage'
            },                       
            element: '#feedback_list_html',
            use: ['queryView', 'templateAutoRender'],
            initialize: function () {
                this.template = Handlebars.compile($('#feedback_list_tpl').html());
                this.query();
            },
            render: function () {
                this.$el.html(this.template(this.model));               
            },           
            query: function () {
            	
                var _this = this;
                var pageNo = this.model.CurrentPage;
                var pageSize = this.model.PageSize;      
                pageSize=3;
                $.postJSON(this.api.query, {pageSize: pageSize, pageNo: pageNo}).then(function (dataSource) {
//                	var dataSource = {
//                			count:'2',
//                    		data:[{contacts:'0',content:'54',demandTitle:'理论政策',demandType:'100',feedback:'1',phone:'毛概'},{contacts:'0',content:'54',demandTitle:'理论政策',demandType:'100',feedback:'',phone:'毛概'}]                    		
//                    };
                	//console.log(dataSource);
                	var feedbackData  = dataSource.data; 
                	for(i in feedbackData){
//                		feedbackData[i].isLook = feedbackData[i].feedback==''?'true':'fales';
                		var content = feedbackData[i].feedbackContent;
                		feedbackData[i].isLook = true;
                		if (content === ""  || content == null) {
                			feedbackData[i].isLook = false;
                		};
                		var type = feedbackData[i].feedbackClass;                		
                		if (type =='0') {
                			feedbackData[i].feedbackType ='意见反馈';
                		};
                		if(type == '1'){
                			feedbackData[i].feedbackType ='课程索取';
                		};
                		if(type == '2'){
                			feedbackData[i].feedbackType ='技术问题';
                		};
                		
                	};                	
                	dataSource.data = feedbackData;                	
                	_.extend(_this.model, dataSource);                	
                    _this.render(); 
                })
            },           
            goTopage: function (e) {
                e.preventDefault();
                this.model.CurrentPage = $(e.target).attr('arg');
                this.query();
            }
        };