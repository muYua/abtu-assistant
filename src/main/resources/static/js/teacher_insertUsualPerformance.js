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

        table.render({
            elem: '#selectStudentInfos'
            , url: utils.getDomainName() + '/student/getStudentInfos' //数据接口
            , where: {
                classId: sessionStorage.getItem("selectedClass"),
            }
            , method: 'get'
            , cellMinWidth: 60 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {type: 'numbers', title: '序号'}
                , {field: 'roleId', title: '学生ID', hide: true}
                , {field: 'stuNumber', title: '学号'}
                , {field: 'name', title: '姓名'}
                , {fixed: 'right', title: '操作', toolbar: '#selectStudentInfosRowBar', align: 'center', width: 160} //行工具栏
            ]]
            , page: true
            , request: {
                pageName: 'pageNo' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }
        });//end render(usualPerformanceInfo)

        // 监听表格行工具栏事件
        table.on('tool(selectStudentInfos)', function (obj) {
            let data = obj.data;//得到所在行所有键值
            if(obj.event === 'do'){ //选择
                layer.prompt({title: '添加平时分', formType: 2}, function(value, index) {
                    layer.close(index);
                    if(!/^(\d{1,2}|100)$/.test(value)){
                        layer.msg("格式错误,输入请输入100及以内的数值", {time: 1000, anim: 6, icon:5});
                        return false;
                    }
                    $.ajax({
                        url: utils.getDomainName() + "/usualPerformance/insertUsualPerformance",
                        data: {
                            courseId: sessionStorage.getItem("selectedCourse"),
                            stuId: data['roleId'],
                            date: utils.getFormatDate("yyyy-MM-dd"),
                            score: value
                        },
                        dataType: 'json',// 服务器返回json格式数据
                        type: 'post',
                        timeout: 10000,// 超时时间设置为10秒
                        success: function (json) {
                            if (json.success) {
                                layer.msg("添加成功！", {time: 1000}, function () {
                                    window.parent.location.reload();
                                });
                            } else {
                                let message = json['msg'];
                                if (typeof message !== undefined && !utils.isEmpty(message))
                                    layer.msg(message);
                                else
                                    layer.msg('添加失败！');
                            }
                        },
                        error: function (xhr, type, errorThrown) {
                            layer.msg('服务器出错，添加失败！');
                            console.log(xhr);
                            console.log(type);
                            console.log(errorThrown);
                        }
                    });//end ajax
                });
            }
            //---------END do-------------
        });//end table.on
    });//end layui.use
});