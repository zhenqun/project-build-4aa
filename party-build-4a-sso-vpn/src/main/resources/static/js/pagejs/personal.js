
		//已完成课程
        var PersonalListView = {
            api: {
                query: '/user/getPersonalList',
            },
            events: {            	            	
            	'click a[cmd=page]': 'goTopage'
            },                       
            element: '#personal_list_html',
            use: ['queryView', 'templateAutoRender'],
            initialize: function () {
                this.template = Handlebars.compile($('#personal_list_tpl').html());
                this.query();
            },
            render: function () {
                this.$el.html(this.template(this.model));               
            },           
            query: function () {
            	var courseType = '' ;            	
                var _this = this;                              
                var studyStatus ='0';
                var pageNo = this.model.CurrentPage;
                var pageSize = this.model.PageSize;                
                $.postJSON(this.api.query, {pageSize: pageSize, pageNo: pageNo,courseType:courseType,studyStatus:studyStatus}).then(function (dataSource) {                	             
                	var personalData  = dataSource.data; 
                	for(i in personalData){                		                	
                		personalData[i].compulsoryFlag = personalData[i].compulsoryFlag==0?'必修':'选修';
                		personalData[i].canTest = personalData[i].studyStatus==1?true:false;
                		personalData[i].canTest2 = personalData[i].finishStatus==0?true:false;
                		//personalData[i].Learning = personalData[i].studyStatus==0?'未开始':'',1?'正在学习,未学完':'',2?'已经学完':'';
                		if(personalData[i].studyStatus==0){
                			personalData[i].Learning = '未开始';
                		}else if (personalData[i].studyStatus==1){
                			personalData[i].Learning = '正在学习,未学完';
                		}
                		else if (personalData[i].studyStatus==2){
                			personalData[i].Learning = '已经学完';
                		}else{
                			personalData[i].Learning = '';
                		} 
                	}
                	dataSource.data = personalData;                	
                	_.extend(_this.model, dataSource);                	
                    _this.render();                                      	
                });
            },            
            goTopage: function (e) {
                e.preventDefault();
                this.model.CurrentPage = $(e.target).attr('arg');
                this.query();
            }
        };
        //tab2
        var PersonalListView2 = {
                api: {
                	query: '/user/getPersonalList',
                },
                events: {                	                	
                	'click a[cmd=page]': 'goTopage'
                },            
                
                element: '#personal_list_html2',
                use: ['queryView', 'templateAutoRender'],
                initialize: function () {
                    this.template = Handlebars.compile($('#personal_list_tpl2').html());
                    this.query();
                },
                render: function () {
                    this.$el.html(this.template(this.model));               
                },               
                query: function () {
                	var courseType = '1' ;            	
                    var _this = this;                
                    var studyStatus ='0';
                    var pageNo = this.model.CurrentPage;
                    var pageSize = this.model.PageSize;                
                    $.postJSON(this.api.query, {pageSize: pageSize, pageNo: pageNo,courseType:courseType,studyStatus:studyStatus}).then(function (dataSource) {
                    	var personalData  = dataSource.data; 
                    	for(i in personalData){                		                	
                    		personalData[i].compulsoryFlag = personalData[i].compulsoryFlag==0?'必修':'选修';
                    		personalData[i].canTest = personalData[i].studyStatus==1?true:false;
                    		personalData[i].canTest2 = personalData[i].finishStatus==0?true:false;
                    		//personalData[i].Learning = personalData[i].studyStatus==0?'未开始':'',1?'正在学习,未学完':'',2?'已经学完':'';
                    		if(personalData[i].studyStatus==0){
                    			personalData[i].Learning = '未开始';
                    		}else if (personalData[i].studyStatus==1){
                    			personalData[i].Learning = '正在学习,未学完';
                    		}
                    		else if (personalData[i].studyStatus==2){
                    			personalData[i].Learning = '已经学完';
                    		}else{
                    			personalData[i].Learning = '';
                    		} 
                    	}
                    	dataSource.data = personalData;                	                	
                    	_.extend(_this.model, dataSource);                	
                        _this.render();                  
                    });
                },                
                goTopage: function (e) {
                    e.preventDefault();
                    this.model.CurrentPage = $(e.target).attr('arg');
                    this.query();
                }
            };
        //tab3
        var PersonalListView3= {
                api: {
                	query:'/user/getPersonalList',
                },
                events: {                	                	
                	'click a[cmd=page]': 'goTopage'
                },                          
                element: '#personal_list_html3',
                use: ['queryView', 'templateAutoRender'],
                initialize: function () {
                    this.template = Handlebars.compile($('#personal_list_tpl3').html());
                    this.query();
                },
                render: function () {
                    this.$el.html(this.template(this.model));               
                },
               
                query: function () {
                	var courseType = '0' ;            	
                    var _this = this;                                   
                    var studyStatus ='0';
                    var pageNo = this.model.CurrentPage;
                    var pageSize = this.model.PageSize;                
                    $.postJSON(this.api.query, {pageSize: pageSize, pageNo: pageNo,courseType:courseType,studyStatus:studyStatus}).then(function (dataSource) {
                    	var personalData  = dataSource.data; 
                    	for(i in personalData){                		                	
                    		personalData[i].compulsoryFlag = personalData[i].compulsoryFlag==0?'必修':'选修';
                    		personalData[i].canTest = personalData[i].studyStatus==1?true:false;
                    		personalData[i].canTest2 = personalData[i].finishStatus==0?true:false;
                    		//personalData[i].Learning = personalData[i].studyStatus==0?'未开始':'',1?'正在学习,未学完':'',2?'已经学完':'';
                    		if(personalData[i].studyStatus==0){
                    			personalData[i].Learning = '未开始';
                    		}else if (personalData[i].studyStatus==1){
                    			personalData[i].Learning = '正在学习,未学完';
                    		}
                    		else if (personalData[i].studyStatus==2){
                    			personalData[i].Learning = '已经学完';
                    		}else{
                    			personalData[i].Learning = '';
                    		} 
                    	}
                    	dataSource.data = personalData;                	                	
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
