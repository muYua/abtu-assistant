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

        let stuId = sessionStorage.getItem("roleId");
        if(utils.isEmpty(stuId)){
            layerAnim6('学生信息为空，删除课程出现错误！');
            return false;
        }


        table.render({
            elem: '#delCourse'
            , url: utils.getDomainName() + '/course/getCourseInfoOfStudent' //数据接口
            , where: {
                stuId: stuId
            }
            , method: 'get'
            , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {field: 'courseId', title: '班级ID'}
                , {field: 'courseName', title: '课程名称'}
                , {field: 'teacherName', title: '任课教师'
                    , templet: function (data) {return data['teacher']['name'];}}
                , {fixed: 'right', title: '操作', toolbar: '#delCourseRowBar', align: 'center'} //行工具栏
            ]]
            , page: true
            , request: {
                pageName: 'pageNo' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }
        });//end render(addCourse)

        table.on('tool(delCourse)', function (obj) { //tool(lay-filter)
            let data = obj.data;
            console.log(data);
            if (obj.event === 'del') {
                $.ajax({
                    url: utils.getDomainName() + "/course/deleteCourseOfStudent/" + stuId + "/" +data.courseId,
                    data: {},
                    dataType: 'json',// 服务器返回json格式数据
                    type: 'delete',
                    timeout: 10000,// 超时时间设置为10秒
                    success: function (json) {
                        if (json.success) {
                            layer.msg("删除成功！", {time: 1000}, function () {
                                sessionStorage.removeItem("selectedCourse");
                                sessionStorage.removeItem("selectedCourseName");
                                window.parent.location.reload();//修改成功后刷新父界面
                                // window.location.reload();
                            });
                        } else {
                            let message = json['msg'];
                            if (typeof message !== undefined && !utils.isEmpty(message) && message !== "")
                                layer.msg(message);
                            else
                                layer.msg('删除失败！');
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
    });//end layui.use
});