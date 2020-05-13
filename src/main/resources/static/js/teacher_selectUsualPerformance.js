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

        $("input[name='insertUsualPerformance']").on("keyup", function () {

            let score = $(this).val().trim();

            if (utils.isPressEnter()) {
                if(utils.verifyLayer("insertUsualPerformance",
                    /^(\d{1,2}|100)$/,
                    "格式错误，请输入100及以内的数值",
                    $, layer)) return false;

                $.ajax({
                    url: utils.getDomainName() + "/usualPerformance/insertUsualPerformance",
                    data: {
                        courseId: sessionStorage.getItem("selectedCourse"),
                        stuId: utils.getUrlParam("stuId"),
                        date: utils.getFormatDate("yyyy-MM-dd"),
                        score: score
                    },
                    dataType: 'json',// 服务器返回json格式数据
                    type: 'post',
                    timeout: 10000,// 超时时间设置为10秒
                    success: function (json) {
                        if (json.success) {
                            layer.msg("新增成功！", {time: 1000}, function () {
                                window.location.reload();//刷新当前页面
                            });
                        } else {
                            let message = json['msg'];
                            if (typeof message !== undefined && !utils.isEmpty(message))
                                layer.msg(message);
                            else
                                layer.msg('新增失败！');
                        }
                    },
                    error: function (xhr, type, errorThrown) {
                        console.log(xhr);
                        console.log(type);
                        console.log(errorThrown);
                    }
                });//end ajax
            }
        });

        table.render({
            elem: '#selectUsualPerformance'
            , url: utils.getDomainName() + '/usualPerformance/getUsualPerformances' //数据接口
            , where: {
                courseId: sessionStorage.getItem("selectedCourse"),
                stuId: utils.getUrlParam("stuId")
            }
            , method: 'get'
            , cellMinWidth: 60 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {field: 'usualPerformanceId', title: 'id', hide: true}
                , {type: 'numbers', title: '序号'}
                , {field: 'stuId', title: '学生ID', hide: true
                    , templet: function (d) {return d['student']['roleId']}}
                , {field: 'stuNumber', title: '学号'
                    , templet: function (d) {return d['student']['stuNumber']}}
                , {field: 'name', title: '姓名'
                    , templet: function (d) {return d['student']['name']}}
                , {field: 'score', title: '平时分'}
                , {field: 'date', title: '日期', width: 102}
                , {fixed: 'right', title: '操作', toolbar: '#selectUsualPerformanceRowBar', align: 'center', width: 160} //行工具栏
            ]]
            , page: true
            , request: {
                pageName: 'pageNo' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }
        });//end render(usualPerformanceInfo)

        // 监听表格行工具栏事件
        table.on('tool(selectUsualPerformance)', function (obj) {
            let data = obj.data;//得到所在行所有键值
            if(obj.event === 'edit'){ //编辑
                layer.prompt({title: '修改平时分', formType: 2}, function(value, index) {
                    layer.close(index);
                    if(!/^(\d{1,2}|100)$/.test(value)){
                        layer.msg("格式错误,输入请输入100及以内的数值", {time: 1000, anim: 6, icon:5});
                        return false;
                    }
                    $.ajax({
                        url: utils.getDomainName() + '/usualPerformance/updateUsualPerformance',
                        data: {
                            id: data['usualPerformanceId'],
                            value: value
                        },
                        dataType: 'json',// 服务器返回json格式数据
                        type: 'put',
                        timeout: 10000,// 超时时间设置为10秒
                        success: function (json) {
                            if (json.success) {
                                layer.msg("修改成功！", {time: 1000}, function () {
                                    //同步更新缓存对应的值
                                    obj.update({
                                        score: value
                                    });
                                    // window.location.reload();//刷新当前页面
                                });
                            } else {
                                let message = json['msg'];
                                if (typeof message !== undefined && !utils.isEmpty(message))
                                    layer.msg(message);
                                else
                                    layer.msg('修改失败！');
                            }
                        },
                        error: function (xhr, type, errorThrown) {
                            console.log(xhr);
                            console.log(type);
                            console.log(errorThrown);
                        }
                    });//end ajax
                });
            }
            //---------END edit-------------
            if (obj.event === 'del') { //删除
                $.ajax({
                    url: utils.getDomainName() + '/usualPerformance/deleteUsualPerformance',
                    data: {
                        id: data['usualPerformanceId']
                    },
                    dataType: 'json',// 服务器返回json格式数据
                    type: 'delete',
                    timeout: 10000,// 超时时间设置为10秒
                    success: function (json) {
                        if (json.success) {
                            layer.msg("删除成功！", {time: 1000}, function () {
                                obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                                // window.location.reload();//刷新当前页面
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
        });//end table.on
    });//end layui.use
});