var GameImage = function (ctx, url) {

    let obj = {
        // 画布
        ctx: ctx,
        // 路径
        url: url,
        // 绘制标志
        drawFlag: 0,
        loadSuc: false,
        // 坐标
        x: 0.0,
        y: 0.0,
        // 图片长款
        width: 0,
        height: 0,
        // 锚点
        anchorX: 0,
        anchorY: 0,
        // 图片
        img: {},
        name: "",
        score: 0,
        next: [],
        walking: false,
        init() {
            this.img = new Image();
            this.img.src = this.url;
            this.img.onload = function () {
                obj.loadSuc = true;
                if (obj.width == 0) {
                    obj.width = obj.img.width;
                }
                if (obj.height == 0) {
                    obj.height = obj.img.height;
                }
                if (obj.drawFlag == 1) {
                    obj.drawFlag = 0;
                    obj.draw();
                }
            }
        },
        setName(name) {
            this.name = name;
        },
        setScore(score) {
            this.score = score;
        },
        setNext(next) {
            this.next = this.next.concat(next);
        },
        draw() {
            if (!this.loadSuc) { this.drawFlag = 1; }
            this.ctx.drawImage(this.img, this.x - this.width * this.anchorX, this.y - this.height * this.anchorY, this.width, this.height);

            if (this.name) {
                fillRoundRect(ctx, this.x - 48, this.y - 56, 128, 48, 10, "rgba(255,255,255,0.6)");
                this.ctx.font = '30px "微软雅黑"';
                this.ctx.textAlign = 'center';
                this.ctx.fillText(this.name, this.x + 16, this.y - 20);
            }

            this.walk();
        },
        walk() {
            //console.log(aarr)
            if (!this.walking && this.next.length) {
                this.walking = true;
                // 对象转数组

                setTimeout(() => {
                    let temp = this.next.shift();
                    this.x = temp.x * 32;
                    this.y = temp.y * 32;
                    this.walking = false;
                }, 100);


            }

        }
    }
    obj.init();
    return obj;
}

let GameCanvas = function () {
    let GameCanvasObj = {
        width: 800,
        height: 600,
        canvas: null,
        fps: 1000 / 60,
        ctx: null,
        // 各种鼠标事件
        onmouseup: function () { },
        onmouseover: function () { },
        onmousemove: function () { },
        onclick: function () { },
        onmousedown: function () { },
        // 游戏帧循环
        update: function (ctx, dt) {
            console.log("")
         },
        setCanvasSize(width, height) {
            this.width = width;
            this.height = height;
            if (this.canvas != null) {
                this.canvas.width = this.width;
                this.canvas.height = this.height;
            }
        },
        initCanvas() {
            this.canvas = document.getElementById('canvas');
            //document.body.appendChild(this.canvas);
            //this.canvas.innerHTML = "您的浏览器不支持 HTML5 canvas 标签。"
            this.canvas.width = this.width;
            this.canvas.height = this.height;
            this.canvas.style.backgroundColor = '#FFF';
            this.canvas.style.border = "1px solid #d3d3d3";

            this.canvas.onmouseup = function (event) {
                GameCanvasObj.onmouseup(event);
            }

            this.canvas.onmouseover = function (event) {
                GameCanvasObj.onmouseover(event);
            }

            this.canvas.onmousemove = function (event) {
                GameCanvasObj.onmousemove(event);
            }

            this.canvas.onmousedown = function (event) {
                GameCanvasObj.onmousedown(event);
            }

            this.canvas.onclick = function (event) {
                GameCanvasObj.onclick(event);
            }

        },
        getContext() {
            this.ctx = this.canvas.getContext("2d");
            return this.ctx;
        },
        clear() {
            this.ctx.clearRect(0, 0, this.width, this.height);
        },
        startGame() {
            // console.log(this.fps)

            setInterval(function () {
                GameCanvasObj.update(GameCanvasObj.ctx, 1000 / GameCanvasObj.fps)
            }, 1000 / this.fps)
        }
    }
    return GameCanvasObj;
}


/**该方法用来绘制一个有填充色的圆角矩形 
     *@param cxt:canvas的上下文环境 
     *@param x:左上角x轴坐标 
     *@param y:左上角y轴坐标 
     *@param width:矩形的宽度 
     *@param height:矩形的高度 
     *@param radius:圆的半径 
     *@param fillColor:填充颜色 
     **/
function fillRoundRect(cxt, x, y, width, height, radius, /*optional*/ fillColor) {
    //圆的直径必然要小于矩形的宽高          
    if (2 * radius > width || 2 * radius > height) { return false; }

    cxt.save();
    cxt.translate(x, y);
    //绘制圆角矩形的各个边  
    drawRoundRectPath(cxt, width, height, radius);
    cxt.fillStyle = fillColor || "#000"; //若是给定了值就用给定的值否则给予默认值  
    cxt.fill();
    cxt.restore();
}
function drawRoundRectPath(cxt, width, height, radius) {
    cxt.beginPath(0);
    //从右下角顺时针绘制，弧度从0到1/2PI  
    cxt.arc(width - radius, height - radius, radius, 0, Math.PI / 2);

    //矩形下边线  
    cxt.lineTo(radius, height);

    //左下角圆弧，弧度从1/2PI到PI  
    cxt.arc(radius, height - radius, radius, Math.PI / 2, Math.PI);

    //矩形左边线  
    cxt.lineTo(0, radius);

    //左上角圆弧，弧度从PI到3/2PI  
    cxt.arc(radius, radius, radius, Math.PI, Math.PI * 3 / 2);

    //上边线  
    cxt.lineTo(width - radius, 0);

    //右上角圆弧  
    cxt.arc(width - radius, radius, radius, Math.PI * 3 / 2, Math.PI * 2);

    //右边线  
    cxt.lineTo(width, height - radius);
    cxt.closePath();
}

export default { GameImage, GameCanvas }