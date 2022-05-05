var bullet = function (text) {
    let obj = {
        text: text,
        start() {
            
            let element = document.createElement('div');
            element.innerHTML = text;
            element.style.cssText = "background-image:url(/danmu.png);position:fixed; background-size:cover; color: #DDD; font-size: 12px; font-weight: bolder; display: flex; align-items: center; padding: 0 15px; width: 170px; height: 75px;"
            document.getElementsByTagName('body')[0].appendChild(element);

            element.style.top = (Math.random() * document.body.clientHeight * 0.8) + "px";

            let i = 0;
            let timer1 = setInterval(() => {
                // console.log('bullet' + timer1);
                if (i < document.body.clientWidth + 220) {
                    element.style.left = (document.body.clientWidth - i++) + "px"
                } else {
                    clearInterval(timer1);
                    document.getElementsByTagName('body')[0].removeChild(element);
                }
            }, 10);

        }
    }

    obj.start();
    return obj;
}


export default bullet