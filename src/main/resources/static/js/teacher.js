require.config({
    baseUrl: 'js',
    paths: {
        jquery: 'lib/jquery-3.4.1.min',
        utils: 'utils/utils',
        layui: '../layui/layui', //layui.js-模块化方式, layui.all.js-非模块化方式
        ckeditor: 'lib/ckeditor/ckeditor',
        ckeditorLanguage: 'lib/ckeditor/zh-cn'
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            deps: ['jquery'], //依赖的模块
            exports: 'layui'
        }
    }
});

require(['layui', 'utils', 'ckeditor', 'ckeditorLanguage'], function (layui, utils, ClassicEditor) {
    layui.use(['table', 'element', 'upload'], function () {
        let table = layui.table
            , element = layui.element //导航的hover效果、二级菜单等功能，需要依赖element模块
            , upload = layui.upload
            , $ = layui.$; //使用layui内置的JQuery，table依赖layer，layer依赖JQuery

        //全局变量
        let teacherId = sessionStorage.getItem("roleId");
        console.log("teacherId" + teacherId);

        //加载头像
        $(function () {
            $.ajax({
                url: utils.getDomainName() + "/uploadFile/getImageUrl/" + teacherId,
                data: {},
                dataType: 'json',// 服务器返回json格式数据
                type: 'get',
                timeout: 10000,// 超时时间设置为10秒
                success: function (json) {
                    if (json.success) {
                        $("#headImage").prop("src", json['obj']);
                    }// end if
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            });//end ajax
        });

        /* 刷新课程 */
        $(flushCourse());//$().ready(function(){}); <=> $(function(){});
        function flushCourse() {
            $.ajax({
                url: utils.getDomainName() + "/course/getCourseList",
                data: {
                    teacherId: teacherId
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
                            //移除本地已选课程、班级信息
                            if (sessionStorage.getItem("selectedCourse") !== null) {
                                sessionStorage.removeItem("selectedCourse");
                            }
                            if (sessionStorage.getItem("selectedCourseName") !== null) {
                                sessionStorage.removeItem("selectedCourseName");
                            }
                            if (sessionStorage.getItem("selectedClass") !== null) {
                                sessionStorage.removeItem("selectedClass");
                            }
                            if (sessionStorage.getItem("selectedClassName") !== null) {
                                sessionStorage.removeItem("selectedClassName");
                            }
                            flushClass();
                            return false;
                        }
                        let content = "";
                        $.each(courseList, function (i, course) {
                            /* 通过String.raw``获取``中的原字符串  */
                            content += String.raw`
                                <dd>
                                    <a id="course_${course['courseId']}" class="course" href="javascript:;">${course['courseName']}</a>
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
                            console.log("初始化selectedCourse:" + courseList[0]['courseId']);
                            sessionStorage.setItem("selectedCourse", courseList[0]['courseId']);
                        }
                        if (sessionStorage.getItem("selectedCourseName") === null) {
                            console.log("初始化selectedCourseName", courseList[0]['courseName']);
                            sessionStorage.setItem("selectedCourseName", courseList[0]['courseName']);
                        }
                        flushClass();
                    } else {
                        console.log("课程信息获取失败！")
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

        /* 刷新班级 */
        function flushClass() {
            let courseId = sessionStorage.getItem("selectedCourse");
            console.log("flushClass-courseId:" + courseId);
            let CLASS_DOM = $("#changeClassDom");
            CLASS_DOM.empty();
            CLASS_DOM.html(`<a id="changeClass" href="javascript:;">选择班级</a>`);
            //如果无有效课程信息
            if (typeof courseId === undefined || courseId == null || courseId === "" || courseId === "-1") {
                console.log("无效课程信息！");
                $("#changeClass").after(`
                    <dl class="layui-nav-child">
                        <dd><a id='class_-1'> 无 </a></dd>
                    </dl>
                `);
                element.render('nav', 'headNav');//重新渲染导航栏
                if (sessionStorage.getItem("status") === null) {//初始化页面
                    checkSignIn();
                } else {
                    console.log("刷新当前页面。");
                    flushCurrentStatus();//刷新当前页面
                }
                return false;
            }
            $.ajax({
                url: utils.getDomainName() + "/stuClass/getClassList",
                data: {
                    courseId: courseId
                },
                dataType: 'json',// 服务器返回json格式数据
                type: 'get',
                timeout: 10000,// 超时时间设置为10秒
                success: function (json) {
                    if (json.success) {
                        let classList = json.data;
                        //没有班级信息
                        if (classList === null || classList.length === 0) {
                            $("#changeClass").after(`
                                <dl class="layui-nav-child">
                                    <dd><a id='class_-1'> 无 </a></dd>
                                </dl>
                            `);
                            element.render('nav', 'headNav');//重新渲染导航栏
                            //移除本地已选班级信息
                            if (sessionStorage.getItem("selectedClass") !== null) {
                                sessionStorage.removeItem("selectedClass");
                            }
                            if (sessionStorage.getItem("selectedClassName") !== null) {
                                sessionStorage.removeItem("selectedClassName");
                            }
                            if (sessionStorage.getItem("status") === null) { //初始化页面
                                console.log("初始化页面，并记录状态status=checkSignIn");
                                checkSignIn();
                                sessionStorage.setItem("status", "checkSignIn");
                            } else { //更新班级后刷新当前页面
                                console.log("刷新当前页面。");
                                flushCurrentStatus();
                            }
                            return false;
                        }
                        let content = "";
                        $.each(classList, function (i, clazz) {
                            /* 通过String.raw``获取``中的原字符串  */
                            content += String.raw`
                            <dd>
                                <a id="class_${clazz.classId}" class="class" href="javascript:;">${clazz.className}</a>
                            </dd>`;
                        });
                        $("#changeClass").after(`
                            <dl class="layui-nav-child">
                                ${content}
                            </dl>
                        `);
                        element.render('nav', 'headNav');//重新渲染导航栏
                        //初始化已选班级信息
                        if (sessionStorage.getItem("selectedClass") === null) {
                            console.log("初始化selectedClass" + classList[0]['classId']);
                            sessionStorage.setItem("selectedClass", classList[0]['classId']);
                        }
                        if (sessionStorage.getItem("selectedClassName") === null) {
                            console.log("初始化selectedClassName" + classList[0]['className']);
                            sessionStorage.setItem("selectedClassName", classList[0]['className']);
                        }
                    }// end if
                    if (sessionStorage.getItem("status") === null) {
                        //初始化
                        console.log("初始化页面，并记录状态status=checkSignIn");
                        checkSignIn();
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

        /*--------------END 刷新班级---------------*/

        /* 刷新当前页面 */
        function flushCurrentStatus() {
            let status = sessionStorage.getItem("status");
            console.log("刷新当前页面。status：" + status);
            switch (status) {
                case "checkSignIn" :
                    checkSignIn();
                    break;
                case "sendHomework" :
                    sendHomework();
                    break;
                case "getHomework" :
                    getHomework();
                    break;
                case "sendTeachingFiles" :
                    sendTeachingFiles();
                    break;
                case "usualPerformance" :
                    usualPerformance();
                    break;
                case "sendMessage" :
                    sendMessage();
                    break;
            }
        }

        /*--------------END 刷新当前页面---------------*/

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
                    //换课程后清掉原课程班级
                    sessionStorage.removeItem("selectedClass");
                    sessionStorage.removeItem("selectedClassName");
                    console.log("刷新班级。");
                    flushClass();
                    break;
                case "class": //选择班级
                    console.log("选择班级");
                    console.log("selectedClassName" + DOM_VALUE);
                    sessionStorage.setItem("selectedClass", DOM_ID.slice(6));
                    sessionStorage.setItem("selectedClassName", DOM_VALUE);
                    flushCurrentStatus();
                    break;
                case "courseSetting": //课程管理
                    if (DOM_VALUE === "新建课程")
                        insertCourse();
                    if (DOM_VALUE === "查询课程信息")
                        selectCourse();
                    break;
                case "classSetting": //班级管理
                    if (DOM_VALUE === "新建班级")
                        insertClass();
                    if (DOM_VALUE === "查询班级信息")
                        selectClass();
                    break;
            }
        });
        /*------------END 监听头部导航栏-----------------*/

        /* 课程管理 */

        //新建课程
        function insertCourse() {
            layer.open({
                type: 2, //iframe层
                area: ['420px', '100px'], //宽高
                fixed: true, //固定
                maxmin: false, //最大小化
                closeBtn: 1, //右上关闭
                shadeClose: false, //点击遮罩关闭
                resize: false, //是否允许拉伸
                move: false,  //禁止拖拽
                title: '新建课程',
                content: [utils.getDomainName() + '/teacher_insertCourse.html', 'no'] //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            });
        }

        //查询课程信息
        function selectCourse() {
            layer.open({
                type: 2, //iframe层
                area: ['650px', '500px'], //宽高
                fixed: true, //固定
                maxmin: false, //最大小化
                closeBtn: 1, //右上关闭
                shadeClose: false, //点击遮罩关闭
                resize: false, //是否允许拉伸
                move: false,  //禁止拖拽
                title: '查询课程信息',
                content: utils.getDomainName() + '/teacher_selectCourse.html', //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
                cancel: function () {
                    // 右上角关闭事件的逻辑
                    window.location.reload();
                },
                end: function () {//该iframe窗口销毁，取消或确认，不携带任何参数。layer.open关闭事件
                    location.reload(); //layer.open关闭刷新
                }
            });
        }

        /*---------------END 课程管理---------------*/

        /* 班级管理 */

        //新建班级
        function insertClass() {
            layer.open({
                type: 2, //iframe层
                area: ['420px', '100px'], //宽高
                fixed: true, //固定
                maxmin: false, //最大小化
                closeBtn: 1, //右上关闭
                shadeClose: false, //点击遮罩关闭
                resize: false, //是否允许拉伸
                move: false,  //禁止拖拽
                title: '新建班级',
                content: [utils.getDomainName() + '/teacher_insertClass.html', 'no'] //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            });
        }

        //查询班级信息
        function selectClass() {
            layer.open({
                type: 2, //iframe层
                area: ['650px', '500px'], //宽高
                fixed: true, //固定
                maxmin: false, //最大小化
                closeBtn: 1, //右上关闭
                shadeClose: false, //点击遮罩关闭
                resize: false, //是否允许拉伸
                move: false,  //禁止拖拽
                title: '查询班级信息',
                content: utils.getDomainName() + '/teacher_selectClass.html', //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
                cancel: function () {
                    // 右上角关闭事件的逻辑
                    window.location.reload();
                },
                end: function () {
                    //窗口销毁，取消或确认
                    location.reload(); //layer.open关闭刷新
                }
            });
        }

        /*----------------END 班级管理----------------*/

        /* 考勤情况 */
        function checkSignIn() {
            $("#checkSignIn").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedClassName = sessionStorage.getItem("selectedClassName")
                , selectedCourse = sessionStorage.getItem("selectedCourse")
                , selectedClass = sessionStorage.getItem("selectedClass");
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            if (selectedCourseName == null || selectedClassName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>教师主页</cite></a>
                            <a><cite>考勤情况</cite></a>
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
                        <a><cite>教师主页</cite></a>
                        <a><cite>考勤情况</cite></a>
                        <a><cite>${selectedCourseName}</cite></a>
                        <a><cite>${selectedClassName}</cite></a>
                    </span>
                </div>
                <table class="layui-hide" id="signIn" lay-filter="signIn"></table>
	            <script type="text/html" id="signInRowBar">
	        		<a class="layui-btn layui-btn-xs layui-btn-xs" lay-event="look">查看</a>
	    		</script>
            `);
            element.render('breadcrumb');//重新进行对面包屑的渲染

            //渲染表格
            table.render({
                elem: '#signIn'
                , url: utils.getDomainName() + '/uploadFile/getSignInFiles' //数据接口
                , where: {
                    courseId: selectedCourse,
                    classId: selectedClass,
                    date: utils.getFormatDate("yyyy-MM-dd"),
                    sort: "s"
                }
                , method: 'get'
                , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                , cols: [[
                    {field: 'fileId', title: '文件ID'}
                    , {field: 'stuNumber', title: '学号', align: 'center', sort: true
                        , templet: function (d) {return d['role']['stuNumber'];}}
                    , {field: 'fileUrl', title: '文件访问路径', align: 'center', hide: true}
                    , {field: 'roleName', title: '学生姓名', align: 'center'}
                    , {field: 'createTime', title: '创建时间', sort: true, align: 'center'} //单元格内容水平居中
                    , {fixed: 'right', title: '操作', toolbar: '#signInRowBar', align: 'center'} //行工具栏
                ]]
                , page: true
                , request: {
                    pageName: 'pageNo' //页码的参数名称，默认：page
                    , limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });//end render(signIn)

            // 监听表格行工具栏事件
            table.on('tool(signIn)', function (obj) { //tool(lay-filter)
                let data = obj.data;
                if (obj.event === 'look') {
                    // window.location.href = data['fileUrl'];
                    window.open(data['fileUrl']);
                }
            });//end table.on

        }//end function checkSignIn
        $("#checkSignIn").on("click", function () {
            checkSignIn();
            sessionStorage.setItem("status", "checkSignIn");
        });
        /*--------------END 考勤情况---------------*/

        /*布置作业*/
        function sendHomework() {
            $("#sendHomework").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedClassName = sessionStorage.getItem("selectedClassName")
                , selectedCourse = sessionStorage.getItem("selectedCourse")
                , selectedClass = sessionStorage.getItem("selectedClass");
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            if (selectedCourseName == null || selectedClassName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>教师主页</cite></a>
                            <a><cite>布置作业</cite></a>
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
                        <a><cite>教师主页</cite></a>
                        <a><cite>布置作业</cite></a>
                        <a><cite>${selectedCourseName}</cite></a>
                        <a><cite>${selectedClassName}</cite></a>
                    </span>
                </div>
                <!-- 写字栏 -->
                <div id="editor"></div>
                <div>
            		<button type="button" class="layui-btn" id="sendHomeworkAction" style="margin-top: 5px;">完成并提交</button>
                </div> 
            	<!-- 布置作业 -->
                <fieldset class="layui-elem-field layui-field-title">
            		<legend>需要上传的文件</legend>
        		</fieldset>
                <!-- 多文件拖拽上传 -->
                <div class="layui-upload">
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
		                  	</tr>
		              	  </thead>
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
                , url: utils.getDomainName() + '/uploadFile/uploadFilesByTeacher' //上传接口
                , data: {
                    roleId: teacherId,
                    courseId: selectedCourse,
                    classId: selectedClass,
                    sort: 'ht'
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

            //CKEditor文本编辑器
            let myEditor; //设置CKEditor的全局变量，方便数据调用
            ClassicEditor
                .create($('#editor')[0], {
                    language: 'zh-cn',  // 中文
                })
                .then(editor => {
                    myEditor = editor;//赋给全局变量
                    // 设置初始值
                    myEditor.setData('');
                })
                .catch(error => {
                    console.error(error);
                });
            //提交数据
            $("#sendHomeworkAction").on("click", function () {
                if (utils.isEmpty(myEditor.getData())) {
                    layer.msg("作业内容为空！", {time: 1000});
                    return false;
                }
                $.ajax({
                    url: utils.getDomainName() + "/message/sendMessage",
                    data: {
                        content: myEditor.getData(),
                        teacherId: teacherId,
                        classId: sessionStorage.getItem("selectedClass"),
                        sort: 'h'
                    },
                    dataType: 'json',// 服务器返回json格式数据
                    type: 'post',
                    timeout: 10000,// 超时时间设置为10秒
                    success: function (json) {
                        if (json.success) {
                            layer.msg('提交成功！', {time: 1000}, function () {
                                myEditor.setData('');
                            });
                        }// end if
                    },
                    error: function (xhr, type, errorThrown) {
                        console.log(xhr);
                        console.log(type);
                        console.log(errorThrown);
                    }
                });//end ajax
            });//end sendHomeworkAction.onclick

        }

        $("#sendHomework").on("click", function () {
            sendHomework();
            sessionStorage.setItem("status", "sendHomework");
        });//end sendHomework.onclick
        /*--------------END 布置作业---------------*/

        /*接收作业*/
        function getHomework() {
            $("#getHomework").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedClassName = sessionStorage.getItem("selectedClassName")
                , selectedCourse = sessionStorage.getItem("selectedCourse")
                , selectedClass = sessionStorage.getItem("selectedClass");

            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            if (selectedCourseName == null || selectedClassName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>教师主页</cite></a>
                            <a><cite>接收作业</cite></a>
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
                        <a><cite>教师主页</cite></a>
                        <a><cite>接收作业</cite></a>
                        <a><cite>${selectedCourseName}</cite></a>
                        <a><cite>${selectedClassName}</cite></a>
                    </span>
                </div>
                <table class="layui-hide" id="homeworkFiles" lay-filter="homeworkFiles"></table>
	            <script type="text/html" id="homeworkFilesRowBar">
	        		<a class="layui-btn layui-btn-xs" lay-event="download">下载</a>
	        		<a class="layui-btn layui-btn-xs" lay-event="open">在线打开</a>
	    		</script>
            `);

            element.render('breadcrumb');//重新进行对面包屑的渲染

            //渲染表格
            table.render({
                elem: '#homeworkFiles'
                , url: utils.getDomainName() + '/uploadFile/getHomeworkFiles' //数据接口
                , where: {
                    courseId: selectedCourse,
                    classId: selectedClass,
                    date: utils.getFormatDate("yyyy-MM-dd"),
                    sort: "hs"
                }
                , method: 'get'
                , cellMinWidth: 60 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                , cols: [[
                    {field: 'fileId', title: '文件ID'}
                    , {field: 'fileName', title: '文件名'} //width 支持：数字、百分比和不填写。你还可以通过 minWidth 参数局部定义当前单元格的最小宽度，layui 2.2.1 新增
                    , {field: 'fileSize', title: '文件大小', align: 'center'}
                    , {
                        field: 'stuNumber', title: '学号', align: 'center'
                        , templet: function (d) {
                            return d.role['stuNumber'];
                        }
                    }
                    , {field: 'roleName', title: '学生姓名', align: 'center'}
                    , {field: 'createTime', title: '创建时间', width: 160, sort: true, align: 'center'} //单元格内容水平居中
                    , {fixed: 'right', title: '操作', width: 150, toolbar: '#homeworkFilesRowBar', align: 'center'} //行工具栏
                ]]
                , page: true
                , request: {
                    pageName: 'pageNo' //页码的参数名称，默认：page
                    , limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });//end render(homeworkFiles)
            //监听行点击事件
            table.on('row(homeworkFiles)', function (obj) {
                //标注选中行样式
                obj.tr.addClass("layui-table-click").siblings().removeClass("layui-table-click");
            });
            // 监听表格行工具栏时间
            table.on('tool(homeworkFiles)', function (obj) { //tool(lay-filter)
                let data = obj.data;
                if (obj.event === 'download') {
                    window.open(utils.getDomainName() + "/uploadFile/downloadFile/" + data['fileId']);
                }// end obj.event==='download'
                if (obj.event === 'open') {//在线打开
                    // window.location.href = utils.getDomainName() + "/po/openWord"
                    POBrowser.openWindowModeless(utils.getDomainName() + '/po/openWord/' + data['fileId']);
                }// end obj.event==='download'
            });// end table.on.tool(homeworkFiles)
        }

        $("#getHomework").on("click", function () {
            getHomework();
            sessionStorage.setItem("status", "getHomework");
        });//end getHomework.onclick
        /*--------------END 接收作业---------------*/

        /*课件下发*/
        function sendTeachingFiles() {
            $("#sendTeachingFiles").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedClassName = sessionStorage.getItem("selectedClassName")
                , selectedCourse = sessionStorage.getItem("selectedCourse")
                , selectedClass = sessionStorage.getItem("selectedClass");
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            if (selectedCourseName == null || selectedClassName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>教师主页</cite></a>
                            <a><cite>课件下发</cite></a>
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
                        <a><cite>教师主页</cite></a>
                        <a><cite>课件下发</cite></a>
                        <a><cite>${selectedCourseName}</cite></a>
                        <a><cite>${selectedClassName}</cite></a>
                    </span>
                </div>
                <!-- 多文件拖拽上传 -->
                <div class="layui-upload">
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
                , url: utils.getDomainName() + '/uploadFile/uploadFilesByTeacher' //上传接口
                , data: {
                    roleId: teacherId,
                    courseId: selectedCourse,
                    classId: selectedClass,
                    sort: 'l'
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

        $("#sendTeachingFiles").on("click", function () {
            sendTeachingFiles();
            sessionStorage.setItem("status", "sendTeachingFiles");
        });//end sendTeachingFiles.onclick
        /*--------------END 课件下发---------------*/

        /*平时成绩*/
        function usualPerformance() {
            $("#usualPerformance").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedClassName = sessionStorage.getItem("selectedClassName")
                , selectedCourse = sessionStorage.getItem("selectedCourse")
                , selectedClass = sessionStorage.getItem("selectedClass");
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            if (selectedCourseName == null || selectedClassName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>教师主页</cite></a>
                            <a><cite>平时成绩</cite></a>
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
                        <a><cite>教师主页</cite></a>
                        <a><cite>平时成绩</cite></a>
                        <a><cite>${selectedCourseName}</cite></a>
                        <a><cite>${selectedClassName}</cite></a>
                    </span>
                </div>
                <table class="layui-hide" id="usualPerformanceInfo" lay-filter="usualPerformanceInfo"></table>
	            <script type="text/html" id="usualPerformanceInfoRowBar">
	        		<a class="layui-btn layui-btn-xs" lay-event="detail">查看详情</a>
	    		</script>
            `);
            element.render('breadcrumb');//重新进行对面包屑的渲染


            table.render({
                elem: '#usualPerformanceInfo'
                , url: utils.getDomainName() + '/usualPerformance/getUsualPerformanceInfo' //数据接口
                , where: {
                    courseId: selectedCourse,
                    classId: selectedClass
                }
                , method: 'get'
                , cellMinWidth: 60 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                , cols: [[
                    {field: 'stuId', title: '学生ID'}
                    , {field: 'stuNumber', title: '学号'}
                    , {field: 'name', title: '姓名'}
                    , {field: 'count', title: '打分次数'
                        , templet: function (d) {return d.count + "次"}}
                    , {field: 'avg', title: '平均分'}
                    , {fixed: 'right', title: '操作', toolbar: '#usualPerformanceInfoRowBar', width: 160, align: 'center'} //行工具栏
                ]]
                , page: true
                , request: {
                    pageName: 'pageNo' //页码的参数名称，默认：page
                    , limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
                , done: function (res, curr, count) {
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
                if (obj.event === 'detail') {
                    layer.open({
                        type: 2, //iframe层
                        area: ['650px', '500px'], //宽高
                        fixed: true, //固定
                        maxmin: false, //最大小化
                        closeBtn: 1, //右上关闭
                        shadeClose: false, //点击遮罩关闭
                        resize: false, //是否允许拉伸
                        move: true,  //拖拽
                        title: '平时成绩详细信息',
                        content: utils.getDomainName() + '/teacher_selectUsualPerformance.html?stuId=' + data.stuId
                    });
                }
            });// end table.on.tool(homeworkFiles)
        }

        $("#usualPerformance").on("click", function () {
            usualPerformance();
            sessionStorage.setItem("status", "usualPerformance");
        });// end usualPerformance.onclick
        /*--------------END 平时成绩---------------*/

        /*消息通知*/
        function sendMessage() {
            $("#sendMessage").addClass("layui-this").siblings().removeClass("layui-this");//选中高亮
            let selectedCourseName = sessionStorage.getItem("selectedCourseName")
                , selectedClassName = sessionStorage.getItem("selectedClassName");
            //页面内容替换并填充
            const CONTENT_BODY_DIV = $("#contentBody");
            CONTENT_BODY_DIV.empty();
            if (selectedCourseName == null || selectedClassName == null) {
                CONTENT_BODY_DIV.html(`
                    <!-- 面包屑 -->
                    <div id="breadcrumb">
                        <span class="layui-breadcrumb">
                            <a><cite>教师主页</cite></a>
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
                        <a><cite>教师主页</cite></a>
                        <a><cite>消息通知</cite></a>
                        <a><cite>${selectedCourseName}</cite></a>
                        <a><cite>${selectedClassName}</cite></a>
                    </span>
                </div>
                <!-- 写字栏 -->
                <div id="editor"></div>
                </div>
                <button type="button" class="layui-btn" id="sendMessageAction" style="margin-top: 5px;">完成并提交</button>
                </div> 
            `);
            element.render('breadcrumb');//重新进行对面包屑的渲染
            //CKEditor文本编辑器
            let myEditor; //设置CKEditor的全局变量，方便数据调用
            ClassicEditor
                .create($('#editor')[0], {
                    language: 'zh-cn',  // 中文
                })
                .then(editor => {
                    myEditor = editor;//赋给全局变量
                    // 设置初始值
                    myEditor.setData('');
                })
                .catch(error => {
                    console.error(error);
                });
            //提交数据
            $("#sendMessageAction").on("click", function () {
                $.ajax({
                    url: utils.getDomainName() + "/message/sendMessage",
                    data: {
                        content: myEditor.getData(),
                        teacherId: teacherId,
                        classId: sessionStorage.getItem("selectedClass"),
                        sort: 'm'
                    },
                    dataType: 'json',// 服务器返回json格式数据
                    type: 'post',
                    timeout: 10000,// 超时时间设置为10秒
                    success: function (data) {
                        if (data.success) {
                            layer.msg('提交消息成功。', {time: 1000}, function () {
                                myEditor.setData('');
                            });
                        }// end if
                    },
                    error: function (xhr, type, errorThrown) {
                        console.log(xhr);
                        console.log(type);
                        console.log(errorThrown);
                    }
                });//end ajax
            });//end sendMessageAction.onclick
        }//end sendMessage()
        $("#sendMessage").on("click", function () {
            sendMessage();
            sessionStorage.setItem("status", "sendMessage");
        });//end sendMessage.onclick
        /*--------------END 消息通知---------------*/

        /* 退出 */
        $("#logout").on("click", function () {
            sessionStorage.removeItem("roleId");
            window.location.href = "index.html";
        });
        /*--------------END 退出---------------*/

        /* 教师基本信息 */
        $("#teacherInfo").on("click", function () {
            layer.open({
                type: 2, //iframe层
                area: ['700px', '510px'], //宽高
                fixed: true, //固定
                maxmin: false, //最大小化
                closeBtn: 1, //右上关闭
                shadeClose: false, //点击遮罩关闭
                resize: false, //是否允许拉伸
                move: false,  //禁止拖拽
                title: '教师信息',
                content: utils.getDomainName() + '/teacherInfo.html'//这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            });
        });
        /*--------------END 教师基本信息---------------*/
    });//end layui
});//end require