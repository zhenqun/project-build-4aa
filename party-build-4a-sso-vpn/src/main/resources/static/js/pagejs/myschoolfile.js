
		//已完成课程
        var myschoolfileListView = {
            api: {
                query: '/user/getschoolfileList',
            },
            events: {            	            	
            	'click a[cmd=page]': 'goTopage'
            },                       
            element: '#myschoolfile_list_html',
            use: ['queryView', 'templateAutoRender'],
            initialize: function () {
                this.template = Handlebars.compile($('#myschoolfile_list_tpl').html());
                this.query();
            },
            render: function () {
                this.$el.html(this.template(this.model));               
            },           
            query: function () {
            	var courseType = '' ;            	
                var _this = this;                             
                var studyStatus ='1';
                var pageNo = this.model.CurrentPage;
                var pageSize = this.model.PageSize;                
                $.postJSON(this.api.query, {pageSize: pageSize, pageNo: pageNo,courseType:courseType,studyStatus:studyStatus}).then(function (dataSource) {
//                	var dataSource = {
//                    		count:'2',
//                    		data:[{compulsoryFlag:'0',courseDuration:'54',classificationName:'理论政策',courseHour:'100',courseId:'1',courseName:'毛概',versionStudyRecord:'0.01',studyStatus:'正在学习',endTime:'2016.9.12',score:'100'},{compulsoryFlag:'1',courseDuration:'45',courseHour:'200',courseId:'2',courseName:'邓小平理论',versionStudyRecord:'0.11',studyStatus:'未开始',classificationName:'业务培训',endTime:'2016.10.29',score:'99'}],
//                    		pageNo:'1',
//                    		pageSize:'10'
//                    };
                	var schoolfileData  = dataSource.data; 
                	for(i in schoolfileData){                		                	
                		schoolfileData[i].compulsoryFlag = schoolfileData[i].compulsoryFlag==0?'必修':'选修';
                	}
                	dataSource.data = schoolfileData;                	                	
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
        var myschoolfileListView2 = {
                api: {
                    query: '/user/getschoolfileList',
                },
                events: {                	                	
                	'click a[cmd=page]': 'goTopage'
                },            
                
                element: '#myschoolfile_list_html2',
                use: ['queryView', 'templateAutoRender'],
                initialize: function () {
                    this.template = Handlebars.compile($('#myschoolfile_list_tpl2').html());
                    this.query();
                },
                render: function () {
                    this.$el.html(this.template(this.model));               
                },                
                query: function () {
                	var courseType = '1' ;            	
                    var _this = this;                            
                    var studyStatus ='1';
                    var pageNo = this.model.CurrentPage;
                    var pageSize = this.model.PageSize;                
                    $.postJSON(this.api.query, {pageSize: pageSize, pageNo: pageNo,courseType:courseType,studyStatus:studyStatus}).then(function (dataSource) {
//                    	var dataSource = {
//                        		count:'2',
//                        		data:[{compulsoryFlag:'0',courseDuration:'54',classificationName:'理论政策',courseHour:'100',courseId:'1',courseName:'毛概',versionStudyRecord:'0.01',studyStatus:'正在学习',endTime:'2016.9.12',score:'100'},{compulsoryFlag:'1',courseDuration:'45',courseHour:'200',courseId:'2',courseName:'邓小平理论',versionStudyRecord:'0.11',studyStatus:'未开始',classificationName:'业务培训',endTime:'2016.10.29',score:'99'}],
//                        		pageNo:'1',
//                        		pageSize:'10'
//                        };
                    	var schoolfileData  = dataSource.data; 
                    	for(i in schoolfileData){                		                	
                    		schoolfileData[i].compulsoryFlag = schoolfileData[i].compulsoryFlag==0?'必修':'选修';
                    	}
                    	dataSource.data = schoolfileData;                	                	
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
        var myschoolfileListView3= {
                api: {
                    query: '/user/getschoolfileList',
                },
                events: {                	                	
                	'click a[cmd=page]': 'goTopage'
                },                          
                element: '#myschoolfile_list_html3',
                use: ['queryView', 'templateAutoRender'],
                initialize: function () {
                    this.template = Handlebars.compile($('#myschoolfile_list_tpl3').html());
                    this.query();
                },
                render: function () {
                    this.$el.html(this.template(this.model));               
                },               
                query: function () {
                	var courseType = '0' ;            	
                    var _this = this;                                   
                    var studyStatus ='1';
                    var pageNo = this.model.CurrentPage;
                    var pageSize = this.model.PageSize;                
                    $.postJSON(this.api.query, {pageSize: pageSize, pageNo: pageNo,courseType:courseType,studyStatus:studyStatus}).then(function (dataSource) {
//                    	var dataSource = {
//                        		count:'2',
//                        		data:[{compulsoryFlag:'0',courseDuration:'54',classificationName:'理论政策',courseHour:'100',courseId:'1',courseName:'毛概',versionStudyRecord:'0.01',studyStatus:'正在学习',endTime:'2016.9.12',score:'100'},{compulsoryFlag:'1',courseDuration:'45',courseHour:'200',courseId:'2',courseName:'邓小平理论',versionStudyRecord:'0.11',studyStatus:'未开始',classificationName:'业务培训',endTime:'2016.10.29',score:'99'}],
//                        		pageNo:'1',
//                        		pageSize:'10'
//                        };
                    	var schoolfileData  = dataSource.data; 
                    	for(i in schoolfileData){                		                	
                    		schoolfileData[i].compulsoryFlag = schoolfileData[i].compulsoryFlag==0?'必修':'选修';
                    	}
                    	dataSource.data = schoolfileData;                	                	
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
