
/**
 * 自定义公共函数
 */
const utils =
{

    timeFormat(time, type = 'datetime') {
        let s = new Date(time);

        if (type == 'date') {
            return s.getFullYear() + '-' + ((s.getMonth() < 9) ? '0' : '') + (s.getMonth() + 1) + '-' + ((s.getDate() < 9) ? '0' : '') + s.getDate();
        } else if (type == 'datetime') {
            return s.getFullYear() + '-' + ((s.getMonth() < 9) ? '0' : '') + (s.getMonth() + 1) + '-' + ((s.getDate() < 9) ? '0' : '') + s.getDate() + ' ' + s.getHours() + ':' + ((s.getMinutes() < 10) ? '0' : '') + s.getMinutes() + ':' + ((s.getSeconds() < 10) ? '0' : '') + s.getSeconds();
        }
    },
    lengthFormat(length) {
        var second = 1000,
            minute = second * 60,
            hour = minute * 60,

            _hour = Math.floor(length / hour),
            _min = Math.floor((length % hour) / minute),
            _sec = Math.floor((length % minute) / second);

        return ((_hour < 9) ? '0' : '') + _hour + ":" + ((_min < 9) ? '0' : '') + _min + ":" + ((_sec < 9) ? '0' : '') + _sec;
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
            if (d_seconds <= 0) {
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