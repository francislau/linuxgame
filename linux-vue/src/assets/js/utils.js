
/**
 * 自定义公共函数
 */
const utils =
{

    /**
         * @name: 自定义alert(确定，取消)
         * @author: camellia
         * @email: guanchao_gc@qq.com
         * @date: 2021-01-23 
         * @param:  data    string  显示的文字
         * @param:  callbackTure    function    点击确定回调函数
         * @param:  callbackFalse   function    点击取消回调函数
         */
    alert(data, btn_true = true, btn_false = true, callbackTure = '', callbackFalse = '') {
        var alert_bg = document.createElement('div');
        var alert_box = document.createElement('div');
        var alert_text = document.createElement('div');

        var alert_btn_true = btn_true ? document.createElement('div') : '';
        var alert_btn_false = btn_false ? document.createElement('div') : '';

        // var textNode = document.createTextNode(data ? data : '')
        var textNode = document.createElement('div');
        var btnText_false = btn_true ? document.createTextNode(' ') : '';
        var btnText_true = btn_false ? document.createTextNode(' ') : '';
        textNode.innerHTML = data ? data : ''
        // 控制背景样式
        utils.setCss(alert_bg, {
            'position': 'fixed',
            'top': '0',
            'left': '0',
            'right': '0',
            'bottom': '0',
            'background-color': 'rgba(0, 0, 0, 0.3)',
            'z-index': '999999999'
        });
        // 控制 提示框样式
        utils.setCss(alert_box, {
            'width': '350px',
            'max-width': '90%',
            'font-size': '18px',
            'text-align': 'center',
            'position': 'absolute',
            'top': '40%',
            'left': '50%',
            'transform': 'translate(-50%, -50%)',
            'border': '21px solid',
            'border-image-source': 'url(/task.png)',
            'border-image-slice': '30 fill',
            'word-break': 'break-all'
        });
        // 控制提示字体样式
        utils.setCss(alert_text, {
            'padding': '32px 15px',
            'margin-left': '-8px',
            'margin-top': '-8px',
            'font-weight': 'bolder',
            'color': '#ddd',
        });
        // 控制确定按钮样式
        if (btn_true)
            utils.setCss(alert_btn_true, {
                'padding': '10px 0',
                'color': '#007aff',
                'font-weight': '600',
                'cursor': 'pointer',
                'float': 'right',
                'margin-rihht': '8px',
                'text-align': 'center',
                'width': '49%',
                'height': '24px',
                'background-image': 'url(/confirm.png)',
                'background-size': '70px',
                'background-position': 'center',
                'background-repeat': 'no-repeat',
            });
        // 控制取消按钮样式
        if (btn_false)
            utils.setCss(alert_btn_false, {
                'padding': '10px 0',
                'color': '#007aff',
                'font-weight': '600',
                'cursor': 'pointer',
                'float': 'left',
                'margin-left': '-8px',
                'text-align': 'center',
                'width': '49%',
                'height': '24px',
                'background-image': 'url(/cancel.png)',
                'background-size': '70px',
                'background-position': 'center',
                'background-repeat': 'no-repeat',
            });
        // 内部结构套入
        alert_text.appendChild(textNode);
        alert_box.appendChild(alert_text);
        if (btn_true) {
            alert_btn_true.appendChild(btnText_true);
            alert_box.appendChild(alert_btn_true);
        }
        if (btn_false) {
            alert_btn_false.appendChild(btnText_false);
            alert_box.appendChild(alert_btn_false);
        }
        alert_bg.appendChild(alert_box);
        // 整体显示到页面内
        document.getElementsByTagName('body')[0].appendChild(alert_bg);
        // 确定按钮绑定点击事件
        if (btn_true)
            alert_btn_true.onclick = function () {
                // alert_bg.parentNode.removeChild(alert_bg);
                if (typeof callbackTure === 'function') {
                    callbackTure(); //回调
                }
                utils.close(alert_bg);
                //  utils.setCss(alert_bg, {
                //      'display': 'none'
                //  });
            }
        // 取消按钮绑定点击事件
        if (btn_false)
            alert_btn_false.onclick = function () {
                if (typeof callbackFalse === 'function') {
                    callbackFalse(); //回调
                }
                else if (typeof callbackTure === 'function') {
                    callbackTure(); //回调
                }

                utils.close(alert_bg);
                //  utils.setCss(alert_bg, {
                //      'display': 'none'
                //  });
            }

        return alert_bg;
    },
    /**
     * @name: 自定义alert添加css
     * @author: camellia
     * @email: guanchao_gc@qq.com
     * @date: 2021-01-23 
     */
    setCss(targetObj, cssObj) {
        var str = targetObj.getAttribute("style") ? targetObj.getAttribute('style') : '';
        for (var i in cssObj) {
            str += i + ':' + cssObj[i] + ';';
        }
        targetObj.style.cssText = str;
    },
    close(targetObj) {
        if (targetObj)
            document.getElementsByTagName('body')[0].removeChild(targetObj)
    },
    danmu() {

    },
    dateDiff(hisTime, nowTime) {
        var now = nowTime ? nowTime.getTime() : new Date().getTime(),
            diffValue = now - hisTime.getTime(),
            result = '',
            second = 1000,
            minute = second * 60,
            hour = minute * 60,
            day = hour * 24,
            halfamonth = day * 15,
            month = day * 30,
            year = month * 12,

            _year = diffValue / year,
            _month = diffValue / month,
            _week = diffValue / (7 * day),
            _day = diffValue / day,
            _hour = diffValue / hour,
            _min = diffValue / minute,
            _sec = diffValue / second;

        if (_year >= 1) result = parseInt(_year) + "年";
        else if (_month >= 1) result = parseInt(_month) + "个月";
        else if (_week >= 1) result = parseInt(_week) + "周";
        else if (_day >= 1) result = parseInt(_day) + "天";
        else if (_hour >= 1) result = parseInt(_hour) + "小时";
        else if (_min >= 1) result = parseInt(_min) + "分钟";
        else result = "已用" + parseInt(_sec) + "秒";
        return result;
    },
    lengthFormat(diffValue) {
        var result = '',
            second = 1000,
            minute = second * 60,
            hour = minute * 60,
            day = hour * 24,
            halfamonth = day * 15,
            month = day * 30,
            year = month * 12,

            _year = diffValue / year,
            _month = diffValue / month,
            _week = diffValue / (7 * day),
            _day = diffValue / day,
            _hour = diffValue / hour,
            _min = diffValue / minute,
            _sec = diffValue / second;

        if (_year >= 1) result = parseInt(_year) + "年";
        else if (_month >= 1) result = parseInt(_month) + "个月";
        else if (_week >= 1) result = parseInt(_week) + "周";
        else if (_day >= 1) result = parseInt(_day) + "天";
        else if (_hour >= 1) result = parseInt(_hour) + "小时";
        else if (_min >= 1) result = parseInt(_min) + "分钟";
        else result = parseInt(_sec) + "秒";
        return result;
    },
    timeFormat(time, type = 'datetime') {
        let s = time ? new Date(time) : new Date();

        if (type == 'date') {
            return s.getFullYear() + '-' + ((s.getMonth() < 9) ? '0' : '') + (s.getMonth() + 1) + '-' + s.getDate();
        } else if (type == 'datetime') {
            return s.getFullYear() + '-' + ((s.getMonth() < 9) ? '0' : '') + (s.getMonth() + 1) + '-' + s.getDate() + ' ' + s.getHours() + ':' + ((s.getMinutes() < 10) ? '0' : '') + s.getMinutes() + ':' + s.getSeconds();
        }
    },
    getDateDiff(dateStr) {
        var publishTime = dateStr / 1000,
            d_seconds,
            d_minutes,
            d_hours,
            d_days,
            timeNow = parseInt(new Date().getTime() / 1000),
            d,

            date = new Date(publishTime * 1000),
            Y = date.getFullYear(),
            M = date.getMonth() + 1,
            D = date.getDate(),
            H = date.getHours(),
            m = date.getMinutes(),
            s = date.getSeconds();
        //小于10的在前面补0
        if (M < 10) {
            M = '0' + M;
        }
        if (D < 10) {
            D = '0' + D;
        }
        if (H < 10) {
            H = '0' + H;
        }
        if (m < 10) {
            m = '0' + m;
        }
        if (s < 10) {
            s = '0' + s;
        }

        d = timeNow - publishTime;
        d_days = parseInt(d / 86400);
        d_hours = parseInt(d / 3600);
        d_minutes = parseInt(d / 60);
        d_seconds = parseInt(d);

        if (d_days > 0 && d_days < 15) {
            return d_days + '天前';
        } else if (d_days <= 0 && d_hours > 0) {
            return d_hours + '小时前';
        } else if (d_hours <= 0 && d_minutes > 0) {
            return d_minutes + '分钟前';
        } else if (d_seconds < 60) {
            if (d_seconds <= 30) {
                return '刚刚';
            } else {
                return d_seconds + '秒前';
            }
        } else if (d_days >= 3 && d_days < 30) {
            return M + '-' + D + ' ' + H + ':' + m;
        } else if (d_days >= 30) {
            return Y + '-' + M + '-' + D + ' ' + H + ':' + m;
        }
    }
}



export { utils }