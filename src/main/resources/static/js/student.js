require.config({
    baseUrl: 'js',
    paths: {
        jquery: 'lib/jquery-3.4.1.min',
        utils: 'utils/utils',
        encrypt: 'utils/encrypt',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            deps: ['jquery'], //依赖的模块
            exports: 'layui'
        }
    }
});

require(['layui', 'utils', 'encrypt'], function (layui, utils, encrypt) {
    layui.use(['table', 'element', 'upload'], function () {
        let table = layui.table
            , element = layui.element //导航的hover效果、二级菜单等功能，需要依赖element模块
            , upload = layui.upload
            , $ = layui.$; //使用layui内置的JQuery，table依赖layer，layer依赖JQuery

        //全局变量
        let stuId = sessionStorage.getItem("roleId");
        console.log("stuId" + stuId);

        $(flushCourse());

        /* 刷新课程 */
        function flushCourse() {
            $.ajax({
                url: utils.getDomainName() + "/course/getCourseListByStuId",
                data: {
                    stuId: stuId
                },
                dataType: 'json',// 服务器返回json格式数据
                type: 'get',
                timeout: 10000,// 超时时间设置为10秒
                success: function (json) {
                    if (json.success) {
                        let courseList = json.data;
                        let COURSE_DOM = $("#changeCourseDom");
                        COURSE_DOM.empty();
                        COURSE_DOM.html(`<a id="changeCourse" href="javascript:;">选择课程</a>`);
                        //没有课程信息
                        if (courseList === null || courseList.length === 0) {
                            console.log("没有课程信息。");
                            //页面渲染
                            $("#changeCourse").after(`
                                <dl class="layui-nav-child">
                                    <dd><a id='course_-1'> 无 </a></dd>
                                </dl>
        	                `);
                            element.render('nav', 'headNav');//重新渲染导航栏
                            //移除本地已选课程信息
                            if (sessionStorage.getItem("selectedCourse") !== null) {
                                sessionStorage.removeItem("selectedCourse");
                            }
                            if (sessionStorage.getItem("selectedCourseName") !== null) {
                                sessionStorage.removeItem("selectedCourseName");
                            }
                            if (sessionStorage.getItem("status") === null) {//初始化页面
                                courseTimetable();
                            } else {
                                console.log("刷新当前页面。");
                                flushCurrentStatus();//刷新当前页面
                            }
                            return false;
                        }
                        let content = "";
                        $.each(courseList, function (i, course) {
                            /* 通过String.raw``获取``中的原字符串  */
                            content += String.raw`
                                <dd>
                                    <a id="course_${course['id']}" class="course" href="javascript:;">${course['courseName']}</a>
                                </dd>`;
                        });
                        $("#changeCourse").after(`
                            <dl class="layui-nav-child">
                                ${content}
                            </dl>
        	            `);
                        element.render('nav', 'headNav');//重新渲染导航栏
                        //初始化本地已选课程信息
                        if (sessionStorage.getItem("selectedCourse") === null) {
                            console.log("初始化selectedCourse:" + courseList[0]['id']);
                            sessionStorage.setItem("selectedCourse", courseList[0]['id']);
                        }
                        if (sessionStorage.getItem("selectedCourseName") === null) {
                            console.log("初始化selectedCourseName", courseList[0]['courseName']);
                            sessionStorage.setItem("selectedCourseName", courseList[0]['courseName']);
                        }
                    }
                    if (sessionStorage.getItem("status") === null) {
                        //初始化
                        console.log("初始化页面，并记录状态status=checkSignIn");
                        courseTimetable();
                        sessionStorage.setItem("status", "checkSignIn");
                    } else { //更新班级后刷新当前页面
                        console.log("刷新当前页面。");
                        flushCurrentStatus();
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            });//end ajax

        }

        /*--------------END 刷新课程---------------*/

        /* 设置课程班级信息，监听头部导航栏 */
        element.on('nav(headNav)', function (elem) {
            let DOM_ID = $(elem).prop('id');
            console.log("DOM_ID" + DOM_ID);
            let DOM_VALUE = $(elem).html(); // elem.text();
            switch ($(elem).prop('class')) {
                case "course": //选择课程
                    console.log("选择课程");
                    console.log("selectedCourseName" + DOM_VALUE);
                    sessionStorage.setItem("selectedCourse", DOM_ID.slice(7));
                    sessionStorage.setItem("selectedCourseName", DOM_VALUE);
                    flushCurrentStatus();
                    break;
                case "addCourse": //添加课程
                    addCourse();
                    flushCurrentStatus();
                    break;
                case "deleteCourse": //删除课程
                    deleteCourse();
                    flushCurrentStatus();
                    break;
            }
        });
        /*------------END 监听头部导航栏-----------------*/

        /* 刷新当前页面 */
        function flushCurrentStatus() {
            let status = sessionStorage.getItem("status");
            console.log("刷新当前页面。status：" + status);
            switch (status) {
                case "courseTimetable" :
                    courseTimetable();
                    break;
                case "showTeachingFiles" :
                    showTeachingFiles();
                    break;
                case "signIn" :
                    signIn();
                    break;
                case "getHomework" :
                    getHomework();
                    break;
                case "submitHomework" :
                    submitHomework();
                    break;
                case "usualPerformance" :
                    usualPerformance();
                    break;
                case "showMessage" :
                    showMessage();
                    break;
            }
        }
        /*--------------END 刷新当前页面---------------*/

        /*添加课程*/
        function addCourse() {
            layer.open({
                type: 2, //iframe层
                area: ['520px', '500px'], //宽高
                fixed: true, //固定
                maxmin: false, //最大小化
                closeBtn: 1, //右上关闭
                shadeClose: false, //点击遮罩关闭
                resize: false, //是否允许拉伸
                move: false,  //禁止拖拽
                title: '添加课程',
                content: utils.getDomainName() + '/student_addCourse.html' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            });
        }
        /*--------------END 添加课程---------------*/

        /*删除课程*/
        function deleteCourse() {
            layer.open({
                type: 2, //iframe层
                area: ['650px', '500px'], //宽高
                fixed: true, //固定
                maxmin: false, //最大小化
                closeBtn: 1, //右上关闭
                shadeClose: false, //点击遮罩关闭
                resize: false, //是否允许拉伸
                move: false,  //禁止拖拽
                title: '删除课程',
                content: utils.getDomainName() + '/student_deleteCourse.html'//这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            });
        }
        /*--------------END 添加课程---------------*/

        /* 实时签到 */
        function signIn () {
            $("#signIn").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮

            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();

            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a href="#">学生主页</a>
                        <a><cite>实时签到</cite></a>
                    </span>
                </div>
                
                <blockquote class="layui-elem-quote">
                    &nbsp;&nbsp;实时签到请使用手机完成！
                </blockquote>  
            `);

            element.render('breadcrumb');//重新进行对面包屑的渲染
        }
        $("#signIn").on("click", function () {
            signIn();
            sessionStorage.setItem("status", "signIn");
        });//end signIn.onclick
        /*--------------END 实时签到---------------*/

        /* 作业查看 */
        function getHomework () {
            $("#getHomework").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedCourse = sessionStorage.getItem("selectedCourse");
            if (selectedCourseName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>学生主页</cite></a>
                            <a><cite>课后作业</cite></a>
                            <a><cite>查看作业</cite></a>
                        </span>
                    </div>
                    <blockquote class="layui-elem-quote">目前没有课程信息！</blockquote>
                `);
                element.render('breadcrumb');//重新进行对面包屑的渲染
                return false;
            }
            CONTENT_BODY_DIV.html(` 		 
            	 <!-- 面包屑 -->
                 <div id="breadcrumb">
                     <span class="layui-breadcrumb">
                         <a>学生主页</a>
                         <a><cite>课后作业</cite></a>
                         <a><cite>查看作业</cite></a>
                         <a><cite>${selectedCourseName}</cite></a>
                     </span>
                 </div>
                 <fieldset class="layui-elem-field layui-field-title">
            		 <legend>布置的作业</legend>
        		 </fieldset>
        		 <div id="messageContent">
                     <blockquote class="layui-elem-quote">
                        &nbsp;&nbsp;作业在这里可以看到！
                     </blockquote>
                 </div>
                 <table class="layui-hide" id="homeworkFiles" lay-filter="homeworkFiles"></table>
                 <script type="text/html" id="homeworkFilesRowBar">
            		 <a class="layui-btn layui-btn-xs layui-btn-xs" lay-event="download">下载</a>
        		 </script>
        		 <fieldset class="layui-elem-field layui-field-title">
            		 <legend>已交的作业</legend>
        		 </fieldset>
                 <table class="layui-hide" id="submittedFiles" lay-filter="submittedFiles"></table>
                 <script type="text/html" id="submittedFilesRowBar">
            		 <a class="layui-btn layui-btn-xs layui-btn-xs" lay-event="download">下载 </a>
            		 <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
        		 </script>
             `);
            element.render('breadcrumb');//重新进行对面包屑的渲染
            $.ajax({
                url: utils.getDomainName() + "/message/getMessage",
                data: {
                    stuId: stuId,
                    courseId: selectedCourse,
                    date: utils.getFormatDate("yyyy-MM-dd"),
                    sort: 'h'
                },
                dataType: 'json',// 服务器返回json格式数据
                type: 'get',
                timeout: 10000,// 超时时间设置为10秒
                success: function (json) {
                    if (json.success) {
                        console.log("获取消息成功。");
                        const MESSAGE_CONTENT_DIV = $("#messageContent");
                        let data = json.data;
                        console.log(data);
                        if (data.length === 0) {
                            return false;
                        }
                        MESSAGE_CONTENT_DIV.empty();
                        let messageContent = "";
                        $.each(data, function (i, message) {
                            messageContent += String.raw`
                                <h5>发布时间：${message['createTime']}</h5>
                                <blockquote class="layui-elem-quote">${message['content']}</blockquote>
                            `;
                        });
                        //填充消息内容
                        MESSAGE_CONTENT_DIV.html(messageContent);
                    }// end if
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            });//end ajax

            //渲染表格
            table.render({
                elem: '#homeworkFiles'
                , url: utils.getDomainName() + '/uploadFile/getHomeworkFilesByStudent' //数据接口
                , where: {
                    courseId: selectedCourse,
                    stuId: stuId,
                    date: utils.getFormatDate("yyyy-MM-dd"),
                    sort: "ht"
                }
                , method: 'get'
                , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                , cols: [[
                    {field: 'id', title: '文件ID'}
                    , {field: 'fileName', title: '文件名'} //width 支持：数字、百分比和不填写。你还可以通过 minWidth 参数局部定义当前单元格的最小宽度，layui 2.2.1 新增
                    , {field: 'fileSize', title: '文件大小', align: 'center'}
                    , {
                        field: 'role', title: '创建人', align: 'center'
                        , templet: function (d) {
                            return d.role['nickname'];
                        }
                    }
                    , {field: 'createTime', title: '创建时间', sort: true, align: 'center'} //单元格内容水平居中
                    , {fixed: 'right', title: '操作', toolbar: '#homeworkFilesRowBar', align: 'center'} //行工具栏
                ]]
                , request: {
                    pageName: 'pageNo' //页码的参数名称，默认：page
                    , limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });//end render(homeworkFiles)
            table.render({
                elem: '#submittedFiles'
                , url: utils.getDomainName() + '/uploadFile/getHomeworkFilesByStudent' //数据接口
                , where: {
                    courseId: selectedCourse,
                    stuId: stuId,
                    date: utils.getFormatDate("yyyy-MM-dd"),
                    sort: "hs"
                }
                , method: 'get'
                , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                , cols: [[
                    {field: 'id', title: '文件ID'}
                    , {field: 'fileName', title: '文件名'} //width 支持：数字、百分比和不填写。你还可以通过 minWidth 参数局部定义当前单元格的最小宽度，layui 2.2.1 新增
                    , {field: 'fileSize', title: '文件大小', align: 'center'}
                    , {
                        field: 'role', title: '创建人', align: 'center'
                        , templet: function (d) {
                            return d.role['nickname'];
                        }
                    }
                    , {field: 'createTime', title: '创建时间', sort: true, align: 'center'} //单元格内容水平居中
                    , {fixed: 'right', title: '操作', toolbar: '#submittedFilesRowBar', align: 'center'} //行工具栏
                ]]
                , request: {
                    pageName: 'pageNo' //页码的参数名称，默认：page
                    , limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });//end render(submittedFiles)
            //监听行点击事件
            table.on('row(homeworkFiles)', function (obj) {
                //标注选中行样式
                obj.tr.addClass("layui-table-click").siblings().removeClass("layui-table-click");
            });
            table.on('row(submittedFiles)', function (obj) {
                //标注选中行样式
                obj.tr.addClass("layui-table-click").siblings().removeClass("layui-table-click");
            });//end 监听行点击事件

            // 监听表格行工具栏时间
            table.on('tool(homeworkFiles)', function (obj) { //tool(lay-filter)
                let data = obj.data;
                console.log(data + '====^1');
                if (obj.event === 'download') {

                }// end obj.event==='download'
            });// end table.on.tool(homeworkFiles)
            table.on('tool(submittedFiles)', function (obj) {
                let data = $(obj.data);
                console.log(data + '====^2');
                if (obj.event === 'download') {
//	            	 window.open(url="http://localhost:8080/assistant/static/"+data.fileName);
                    const LINK = $(`<a href='${data['fileUrl']}' download='${data.fileName}'>download</a>`);
                    $(document.body).append(LINK);
                    $(LINK).click();
                    $(LINK).remove();
                }// end obj.event==='download'
                if (obj.event === 'del') {
                    $.ajax({
                        url: "/assistant/student/delFile",
                        data: {},
                        dataType: 'json',// 服务器返回json格式数据
                        type: 'get',// HTTP请求类型
                        timeout: 10000,// 超时时间设置为10秒
                        success: function (data) {
                            if (data.success) {
                                // 重载数据表格
                                table.reload('submittedFiles'); // end table.reload
                            }// end if
                        },
                        error: function (xhr, type, errorThrown) {
                            console.log(xhr);
                            console.log(type);
                            console.log(errorThrown);
                        }
                    });// end ajax
                }// end obj.event==='del'
            });// end table.on.tool(submittedFiles)

        }
        $("#getHomework").on("click", function () {
            getHomework();
            sessionStorage.setItem("status", "getHomework");
        });//end getHomework.onclick
        /*--------------END 作业查看---------------*/

        /* 作业提交 */
        function submitHomework () {
            $("#submitHomework").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedCourse = sessionStorage.getItem("selectedCourse");
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            if (selectedCourseName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>学生主页</cite></a>
                            <a><cite>提交作业</cite></a>
                        </span>
                    </div>
                    <blockquote class="layui-elem-quote">目前没有课程信息！</blockquote>
                `);
                element.render('breadcrumb');//重新进行对面包屑的渲染
                return false;
            }
            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a><cite>学生主页</cite></a>
                        <a><cite>提交作业</cite></a>
                        <a><cite>${selectedCourseName}</cite></a>
                    </span>
                </div>
                <!-- 多文件拖拽上传 -->
                <div class="layui-upload">
<!--                <button type="button" class="layui-btn layui-btn-normal" id="testList">选择多文件</button> -->
                <div class="layui-upload-drag" id="upload">
                    <i class="layui-icon"></i>
                    <p>点击上传，或将文件拖拽到此处</p>
                </div>
                <div class="layui-upload-list">
                <table class="layui-table">
                  <thead>
                    <tr><th>文件名</th>
                    <th>大小</th>
                    <th>状态</th>
                    <th>操作</th>
                  </tr></thead>
                  <tbody id="uploadList"></tbody>
                </table>
                </div>
                <button type="button" class="layui-btn" id="uploadListAction">开始上传</button>
                </div> 
            `);

            element.render('breadcrumb');//重新进行对面包屑的渲染

            //多文件上传列表
            let uploadListView = $('#uploadList')
                , uploadListIns = upload.render({
                elem: '#upload'
                , url: utils.getDomainName() + '/uploadFile/uploadFilesByStudent' //上传接口
                , data: {
                    roleId: stuId,
                    courseId: selectedCourse,
                    sort: "hs"
                }
                , accept: 'file' //允许上传的文件类型,普通文件
                , multiple: true
                , auto: false
                , bindAction: '#uploadListAction'
                , choose: function (obj) {
                    let files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
                    //读取本地文件
                    obj.preview(function (index, file, result) {
                        let tr = $(['<tr id="upload-' + index + '">'
                            , '<td>' + file.name + '</td>'
                            , '<td>' + (file.size / 1024).toFixed(1) + 'kb</td>'
                            , '<td>等待上传</td>'
                            , '<td>'
                            , '<button class="layui-btn layui-btn-xs upload-reload layui-hide">重传</button>'
                            , '<button class="layui-btn layui-btn-xs layui-btn-danger upload-delete">删除</button>'
                            , '</td>'
                            , '</tr>'].join(''));

                        //单个重传
                        tr.find('.upload-reload').on('click', function () {
                            obj.upload(index, file);
                        });

                        //删除
                        tr.find('.upload-delete').on('click', function () {
                            delete files[index]; //删除对应的文件
                            tr.remove();
                            uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                        });

                        uploadListView.append(tr);
                    });
                }
                , done: function (res, index, upload) {
                    if (res.success) { //上传成功
                        let tr = uploadListView.find('tr#upload-' + index)
                            , tds = tr.children();
                        tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                        tds.eq(3).html(''); //清空操作
                        return delete this.files[index]; //删除文件队列已经上传成功的文件
                    }
                    this.error(index, upload);
                }
                , error: function (index, upload) {
                    let tr = uploadListView.find('tr#upload-' + index)
                        , tds = tr.children();
                    tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
                    tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
                }
            });//end upload.render

        }
        $("#submitHomework").on("click", function () {
            submitHomework();
            sessionStorage.setItem("status", "submitHomework");
        });//end submitHomework.onclick
        /*--------------END 作业提交---------------*/

        /*课堂文件*/
        function showTeachingFiles () {
            $("#showTeachingFiles").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedCourse = sessionStorage.getItem("selectedCourse");
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            if (selectedCourseName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>学生主页</cite></a>
                            <a><cite>课堂文件</cite></a>
                        </span>
                    </div>
                    <blockquote class="layui-elem-quote">目前没有课程信息！</blockquote>
                `);
                element.render('breadcrumb');//重新进行对面包屑的渲染
                return false;
            }
            CONTENT_BODY_DIV.html(` 		 
           	 <!-- 面包屑 -->
             <div id="breadcrumb">
             <span class="layui-breadcrumb">
                 <a><cite>学生主页</cite></a>
                 <a><cite>课堂文件</cite></a>
                 <a><cite>${selectedCourseName}</cite></a>
             </span>
             </div>
             <table class="layui-hide" id="teachingFiles" lay-filter="teachingFiles"></table>
             <script type="text/html" id="teachingFilesRowBar">
           		 <a class="layui-btn layui-btn-xs layui-btn-xs" lay-event="download">下载</a>
       		 </script>
            `);
            element.render('breadcrumb');//重新进行对面包屑的渲染
            //渲染表格
            table.render({
                elem: '#teachingFiles'
                , url: utils.getDomainName() + '/uploadFile/getTeachingFiles' //数据接口
                , where: {
                    courseId: selectedCourse,
                    stuId: stuId,
                    date: utils.getFormatDate("yyyy-MM-dd"),
                    sort: "l"
                }
                , method: 'get'
                , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                , cols: [[
                    {field: 'id', title: '文件ID'}
                    , {field: 'fileName', title: '文件名'} //width 支持：数字、百分比和不填写。你还可以通过 minWidth 参数局部定义当前单元格的最小宽度，layui 2.2.1 新增
                    , {field: 'fileSize', title: '文件大小', align: 'center'}
                    , {field: 'roleName', title: '创建人', align: 'center'}
                    , {field: 'createTime', title: '创建时间', sort: true, align: 'center'} //单元格内容水平居中
                    , {field: 'fileUrl', title: '文件获取路径', hide: true}
                    , {fixed: 'right', title: '操作', toolbar: '#teachingFilesRowBar', align: 'center'} //行工具栏
                ]]
                , page: false
                , request: {
                    pageName: 'pageNo' //页码的参数名称，默认：page
                    , limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });//end render(teachingFiles)
            table.on('row(teachingFiles)', function (obj) {
                //标注选中行样式
                obj.tr.addClass("layui-table-click").siblings().removeClass("layui-table-click");
            });//end 监听行点击事件
            table.on('tool(teachingFiles)', function (obj) {
                let data = $(obj.data);
                console.log(data + '====^2');
                if (obj.event === 'download') {
                    window.open(data['fileUrl']);
//                    const LINK = $(`<a href='http://localhost:8080/assistant/static/${data.fileName}' download='${data.fileName}'>download</a>`);
//                    $(document.body).append(LINK); 
//                    $(LINK).click();
//                    $(LINK).remove();
                }// end obj.event==='download'
            });//end table.on.tool(teachingFiles)
        }
        $("#showTeachingFiles").on("click", function () {
            showTeachingFiles();
            sessionStorage.setItem("status", "showTeachingFiles");
        });//end teachingFiiles.onclick
        /*--------------END 课堂文件---------------*/

        /*查询课表*/
        function courseTimetable() {
            $("#courseTimetable").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a><cite>学生主页</cite></a>
                        <a><cite>查询课表</cite></a>
                    </span>
                </div>
                <table class="layui-hide" id="courseTimetableInfo" lay-filter="courseTimetableInfo"></table>
            `);
            element.render('breadcrumb');//重新进行对面包屑的渲染
            //渲染表格
            table.render({
                elem: '#courseTimetableInfo'
                , url: utils.getDomainName() + '/course/getCourseInfoOfStudent' //数据接口
                , where: {
                    stuId: stuId,
                }
                , method: 'get'
                , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                , cols: [[
                    {field: 'id', title: '课程ID'}
                    , {field: 'courseName', title: '课程名'}
                    , {field: 'teacherName', title: '任课教师'}
                    , {field: 'className', title: '所属班级'}
                ]]
                , page: false
            });//end render(signIn)
            //监听行点击事件
            table.on('row(courseTimetableInfo)', function (obj) {
                //标注选中行样式
                obj.tr.addClass("layui-table-click").siblings().removeClass("layui-table-click");
            });
        }
        $("#courseTimetable").on("click", function () {
            courseTimetable();
            sessionStorage.setItem("status", "courseTimetable");
        });// end courseTimetable.onclick
        /*--------------END 查询课表---------------*/

        /*平时成绩*/
        function usualPerformance () {
            $("#usualPerformance").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedCourse = sessionStorage.getItem("selectedCourse");
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            if (selectedCourseName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>学生主页</cite></a>
                            <a><cite>平时成绩</cite></a>
                        </span>
                    </div>
                    <blockquote class="layui-elem-quote">目前没有课程信息！</blockquote>
                `);
                element.render('breadcrumb');//重新进行对面包屑的渲染
                return false;
            }
            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a><cite>学生主页</cite></a>
                        <a><cite>平时成绩</cite></a>
                        <a><cite>${selectedCourseName}</cite></a>
                    </span>
                </div>
                <table class="layui-hide" id="usualPerformanceInfo" lay-filter="usualPerformanceInfo"></table>
	            <script type="text/html" id="usualPerformanceInfoRowBar">
	        		<a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
	        		<a class="layui-btn layui-btn-xs" lay-event="add">添加</a>
	        		<a class="layui-btn layui-btn-xs" lay-event="del">删除</a>
	    		</script>
            `);
            element.render('breadcrumb');//重新进行对面包屑的渲染


            table.render({
                elem: '#usualPerformanceInfo'
                , url: utils.getDomainName() + '/usualPerformance/getUsualPerformances' //数据接口
                , where: {
                    courseId: selectedCourse,
                    stuId: stuId
                }
                , method: 'get'
                , cellMinWidth: 60 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                , cols: [[
                    {field: 'id', title: '学生ID'}
                    , {field: 'stuNumber', title: '学号'}
                    , {field: 'stuName', title: '学生姓名'}
                    , {fixed: 'right', title: '操作', toolbar: '#usualPerformanceInfoRowBar', align: 'center'} //行工具栏
                ]]
                , page: true
                , request: {
                    pageName: 'pageNo' //页码的参数名称，默认：page
                    , limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
                , done: function (res, curr, count) {
                    let myData = [];
                    let dataList = res.data;
                    let number;
                    myData[0] = {field: 'stuId', title: '学生ID'};
                    myData[1] = {field: 'stuNumber', title: '学号'};
                    myData[2] = {field: 'stuName', title: '学生姓名', align: 'center'};
                    $.each(dataList, function (i, data) {
                        console.log(i+'次');
                        //data.date -> i
                        //数据 -> data.score
                        myData[i] = {field: '' + data.score, title: '第' + i + '次'};
                        number = i;
                    });
                    myData[number+1] = {fixed: 'right', title: '操作', toolbar: '#usualPerformanceInfoRowBar', align: 'center'};//行工具栏
                    table.init('userFilter', { //转换成静态表格
                        cols: [myData]
                        , data: res.data
                        , limit: 10
                    });
                }
            });//end render(usualPerformance)
            //监听行点击事件
            table.on('row(usualPerformanceInfo)', function (obj) {
                //标注选中行样式
                obj.tr.addClass("layui-table-click").siblings().removeClass("layui-table-click");
            });
            // 监听表格行工具栏时间
            table.on('tool(usualPerformanceInfo)', function (obj) { //tool(lay-filter)
                let data = obj.data;
                if (obj.event === 'edit') {
                    // layer.open({
                    //     type: 2, //iframe层
                    //     area: ['420px', '600px'], //宽高
                    //     fixed: true, //固定
                    //     maxmin: false, //最大小化
                    //     closeBtn: 1, //右上关闭
                    //     shadeClose: false, //点击遮罩关闭
                    //     resize: false, //是否允许拉伸
                    //     move: false,  //禁止拖拽
                    //     title: '编辑平时成绩',
                    //     content: utils.getDomainName() + '/teacher_usualPerformance.html' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
                    // });
                }
            });// end table.on.tool(homeworkFiles)
        }
        $("#usualPerformance").on("click", function () {
            usualPerformance();
            sessionStorage.setItem("status", "usualPerformance");
        });// end usualPerformance.onclick
        /*--------------END 平时成绩---------------*/

        /*消息通知*/
        function showMessage() {
            $("#showMessage").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedCourse = sessionStorage.getItem("selectedCourse");
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            if (selectedCourseName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>学生主页</cite></a>
                            <a><cite>消息通知</cite></a>
                        </span>
                    </div>
                    <blockquote class="layui-elem-quote">目前没有课程或班级信息！</blockquote>
                `);
                element.render('breadcrumb');//重新进行对面包屑的渲染
                return false;
            }
            CONTENT_BODY_DIV.html(`
                <!-- 面包屑 -->
                <div id="breadcrumb">
                    <span class="layui-breadcrumb">
                        <a><cite>学生主页</cite></a>
                        <a><cite>${selectedCourseName}</cite></a>
                        <a><cite>消息通知</cite></a>
                    </span>
                </div>
                <div id="messageContent">
                    <blockquote class="layui-elem-quote">消息通知在此处可见。</blockquote>
                </div>
            `);
            element.render('breadcrumb');//重新进行对面包屑的渲染
            $.ajax({
                url: utils.getDomainName() + "/message/getMessage",
                data: {
                    stuId: stuId,
                    courseId: selectedCourse,
                    date: utils.getFormatDate("yyyy-MM-dd"),
                    sort: 'm'
                },
                dataType: 'json',// 服务器返回json格式数据
                type: 'get',
                timeout: 10000,// 超时时间设置为10秒
                success: function (json) {
                    if (json.success) {
                        console.log("获取消息成功。");
                        const MESSAGE_CONTENT_DIV = $("#messageContent");
                        let data = json.data;
                        console.log(data);
                        if (data.length === 0) {
                            return false;
                        }
                        MESSAGE_CONTENT_DIV.empty();
                        let messageContent = "";
                        $.each(data, function (i, message) {
                            messageContent += String.raw`
                                <h5>消息发布时间：${message['createTime']}</h5>
                                <blockquote class="layui-elem-quote">${message['content']}</blockquote>
                            `;
                        });
                        //填充消息内容
                        MESSAGE_CONTENT_DIV.html(messageContent);
                    }// end if
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            });//end ajax
        }
        $("#showMessage").on("click", function () {
            showMessage();
            sessionStorage.setItem("status", "showMessage");
        });// end showMessage.onclick
        /*--------------END 消息通知---------------*/

        /* 注销 */
        $("#logout").on("click", function () {
            sessionStorage.removeItem("roleId");
            window.location.href = "index.html";
        });
        /*--------------END 注销---------------*/
    });//end layui
});//end require