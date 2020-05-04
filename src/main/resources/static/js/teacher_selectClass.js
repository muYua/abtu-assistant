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
        let table = layui.table
            , layer = layui.layer
            , $ = layui.$;

        let courseId = sessionStorage.getItem("selectedCourse");

        table.render({
            elem: '#selectClass'
            , url: utils.getDomainName() + '/stuClass/getClassByPage' //数据接口
            , where: {
                courseId: courseId
            }
            , method: 'get'
            , cellMinWidth: 60 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {field: 'classId', title: '班级ID'}
                , {field: 'className', title: '班级名称'}
                , {field: 'teacherName', title: '任课教师'}
                , {fixed: 'right', title: '操作', toolbar: '#selectClassRowBar', align: 'center', width: 165} //行工具栏
            ]]
            , page: true
            , request: {
                pageName: 'pageNo' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }
        });//end render(selectClass)

        // 监听表格行工具栏事件
        table.on('tool(selectClass)', function (obj) { //tool(lay-filter)
            let data = obj.data;
            console.log(data);
            if (obj.event === 'del') {
                $.ajax({
                    url: utils.getDomainName() + "/stuClass/deleteClass",
                    data: {
                        classId: data['classId']
                    },
                    dataType: 'json',// 服务器返回json格式数据
                    type: 'delete',
                    timeout: 10000,// 超时时间设置为10秒
                    success: function (json) {
                        if (json.success) {
                            layer.msg("删除成功！", {time: 1000}, function () {
                                // window.parent.location.reload();//成功后刷新父界面
                                window.location.reload();
                            });
                        } else {
                            let message = json['msg'];
                            if (typeof message !== undefined && !utils.isEmpty(message))
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
            //---------END del-------------
            if(obj.event === 'detail'){ //查看

            }
            //---------END detail-------------
            if(obj.event === 'edit'){ //编辑

            }
            //---------END edit-------------
        });//end table.on
    });//end layui.use
});