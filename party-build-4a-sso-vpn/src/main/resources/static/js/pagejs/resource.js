
		//绑学习资源库列表
        var ResourceListView = {
            api: {
                query: '/user/getResourceList',
                delStudy:'/user/delCustCourse',
                addStudy:'/user/addCustCourse'
            },
            events: {
            	'click a[cmd=study_add]': 'studyAdd',
            	'click a[cmd=page]': 'goTopage'
            },
            element: '#resource_list_html',
            use: ['queryView', 'templateAutoRender'],
            initialize: function () {
                this.template = Handlebars.compile($('#resource_list_tpl').html()); 
                this.queryStart();
            },
            render: function () {
//            	debugger
            	//this.autoRender();
                this.$el.html(this.template(this.model));
                
            },
            queryStart:function (){
            	this.model.CurrentPage=1;
            	this.query();
            },
            query: function () { 
                var _this = this;
                var pageNo = this.model.CurrentPage;
                var pageSize = this.model.PageSize; 
                var postForm=  {pageSize: pageSize, pageNo: pageNo};
                var compulsoryFlag = $("#compulsoryFlag").val();
    		    var classificationId = $("#classificationId").val();
    		    var keyword = $("#txtKeyWord").val();
                if(compulsoryFlag!="-1"){
                	postForm.compulsoryFlag=compulsoryFlag;
                }
                if(classificationId!="-1"){
                	postForm.classificationId = classificationId;
                } 
                if(keyword!=""){
                	postForm.keyword = keyword;
                }
                $.postJSON(this.api.query, postForm).then(function (dataSource) {
                	var data  = dataSource.data; 
                	for(i in data){ 
                		data[i].studyStatusBool  = data[i].studyStatus=="-1"?false:true; 
                		data[i].createDate = new Date(data[i].createTime); 
                    	data[i].compulsoryFlag = data[i].compulsoryFlag=="0"?'选修':'必修';
                	}
                	dataSource.data = data;
                    _.extend(_this.model, dataSource); 
                   /* _this.model.Items = _.map(_this.model.Items, function (item) {
                    	item.createDate = new Date(item.createTime);
                    	console.log(item.createDate);
                    	item.versionCourse = item.versionCourse==0?'必修':'选修';
                    });*/ 
                    _this.render(); 
                })
            },
            studyAdd: function (e) {
                e.preventDefault(); 
                var courseId = $(e.target).attr('courseId');  
                var versionStudyRecord = $(e.target).attr('versionStudyRecord'); 
                if($(e.target).hasClass("rbox-list-add2")){
                	$.postJSON(this.api.addStudy,{courseId:courseId }).then(function(bool){
                		if(bool){
                			$(e.target).removeClass("rbox-list-add2").addClass("rbox-list-add");
                			getOutTime();
                			toastr.success("添加成功！");
                		}else{
                			toastr.error("添加失败！");
                		}
                	});
                }else{
                	$.postJSON(this.api.delStudy,{courseId:courseId,versionStudyRecord:versionStudyRecord}).then(function(bool){
                		if(bool){
                			$(e.target).removeClass("rbox-list-add").addClass("rbox-list-add2");
                			getOutTime();
                			toastr.success("移除成功！");
                		}else{ 
                			toastr.error("移除失败！");
                		}
                	}); 
                } 
            },
            goTopage: function (e) {
                e.preventDefault();
                this.model.CurrentPage = $(e.target).attr('arg');
                this.query();
            }
        };
        
        
        