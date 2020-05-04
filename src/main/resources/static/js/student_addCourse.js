require.config({
    baseUrl: 'js',
    paths: {
        utils: 'utils/utils',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            exports: 'layui'
        }
    }
});

require(['layui', 'utils'], function (layui, utils) {
    layui.use(['form', 'layer', 'table'], function () {
        let layer = layui.layer
            , table = layui.table
            , $ = layui.$;

        function layerAnim6(msg) {
            layer.msg(msg, {
                anim: 6, //抖动
                time: 1000 //1秒关闭（如果不配置，默认是3秒）
            })
        }

        /* 监听表单提交 */
        $("input[name='courseIdButton']").on("click", function () {
            let COURSE_ID = $("input[name='courseId']")
                , COURSE_ID_VALUE = COURSE_ID.val();

            if(utils.isEmpty(COURSE_ID_VALUE)){
                layerAnim6('请输入课程ID！');
                COURSE_ID.focus();
                return false;
            }

            let stuId = sessionStorage.getItem("roleId");
            if(utils.isEmpty(stuId)){
                layerAnim6('学生信息为空，添加课程出现错误！');
                return false;
            }

            table.render({
                elem: '#addCourse'
                , url: utils.getDomainName() + '/stuClass/getClassByPage' //数据接口
                , where: {
                    courseId: COURSE_ID_VALUE
                }
                , method: 'get'
                , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                , cols: [[
                    {field: 'classId', title: '班级ID'}
                    , {field: 'className', title: '班级名称'}
                    , {field: 'teacherName', title: '任课教师'}
                    , {fixed: 'right', title: '操作', toolbar: '#addCourseRowBar', align: 'center'} //行工具栏
                ]]
                , page: true
                , request: {
                    pageName: 'pageNo' //页码的参数名称，默认：page
                    , limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
            });//end render(addCourse)

            table.on('tool(addCourse)', function (obj) { //tool(lay-filter)
                let data = obj.data;
                console.log(data);
                if (obj.event === 'choose') {
                    $.ajax({
                        url: utils.getDomainName() + "/course/addCourseOfStudent",
                        data: {
                            courseId: COURSE_ID_VALUE,
                            classId: data['classId'],
                            stuId: stuId
                        },
                        dataType: 'json',// 服务器返回json格式数据
                        type: 'post',
                        timeout: 10000,// 超时时间设置为10秒
                        success: function (json) {
                            if (json.success) {
                                layer.msg("添加成功！", {time: 1000}, function () {
                                    sessionStorage.setItem("selectedCourse", COURSE_ID_VALUE);
                                    sessionStorage.setItem("selectedCourseName", json.map['courseName']);
                                    window.parent.location.reload();//修改成功后刷新父界面
                                    // window.location.reload();
                                });
                            } else {
                                let message = json['msg'];
                                if (typeof message !== undefined && !utils.isEmpty(message) && message !== "")
                                    layer.msg(message);
                                else
                                    layer.msg('添加失败！');
                            }
                        },
                        error: function (xhr, type, errorThrown) {
                            console.log(xhr);
                            console.log(type);
                            console.log(errorThrown);
                        }
                    });//end ajax
                }
            });//end table.on
        });
        /*-----------END 监听表单提交------------*/
    });//end layui.use
});