var Node = function (x, y) {
    this.x = x;
    this.y = y;
    this.g = 0; // 到起点的长度
    this.h = 0; // 到终点的长度
    this.p = null; // 父节点
}

/**
 * 实现一个最小堆，方便从OpenSet中选择最小代价的点
 */
var heapMin = function () {
    this.set = [];
}

heapMin.prototype.adjust = function (index) {
    let len = this.set.length,
        l = index * 2 + 1,
        r = index * 2 + 2,
        min = index,
        node = null;

    if (l <= len - 1 && this.set[min].g + this.set[min].h > this.set[l].g + this.set[l].h) {
        min = l;
    }
    if (r <= len - 1 && this.set[min].g + this.set[min].h > this.set[r].g + this.set[r].h) {
        min = r;
    }

    // 如果min发生改变，则需要进行交换，并继续递归min子树
    if (min != index) {
        node = this.set[min];
        this.set[min] = this.set[index];
        this.set[index] = node;
        this.adjust(min);
    }
}

/**
 * 向最小堆中添加一个元素
 */
heapMin.prototype.push = function (node) {
    // 添加到数组尾部
    this.set.push(node);

    // 调整堆
    for (let i = Math.floor(this.set.length / 2) - 1; i >= 0; i--) {
        this.adjust(i);
    }

}

/**
 * 从最小堆中移除顶部元素
 */
heapMin.prototype.pop = function () {
    // 移除顶部元素，为最小元素
    let node = this.set.shift();

    // 调整堆
    this.adjust(0);

    return node;
}

/**
 * 检查堆是否为空
 */
heapMin.prototype.empty = function () {
    return this.set.length > 0 ? false : true;
}

/**
 * 检查堆是否包含指定元素
 */
heapMin.prototype.contain = function (node) {
    for (let len = this.set.length, i = 0; i < len; i++) {
        if (this.set[i].x === node.x && this.set[i].y === node.y) return true;
    }
    return false;
}


/**
 * Set类
 */
function Set() {
    this.set = [];
}

Set.prototype.push = function (node) {
    this.set.push(node);
}

Set.prototype.pop = function () {
    return this.set.pop();
}

Set.prototype.contain = function (node) {
    for (let len = this.set.length, i = 0; i < len; i++) {
        if (this.set[i].x === node.x && this.set[i].y === node.y) return true;// 存在
    }
    return false;
}

/**
 * AStarPathFinding
 * 
 * W：地图的宽度
 * H：地图的高度
 * map：地图数组，0表示可以通过，1表示不可以通过
 *
 */
function AStarPathFinding(W, H, map) {
    this.W = W;// 地图的宽度
    this.H = H;// 地图的高度
    this.map = map;// 地图
}

/**
 * 计算距离
 *
 * 采用曼哈顿估量算法
 */
function calcDistance(startNode, endNode) {
    return (startNode && endNode) ?
        Math.abs(startNode.x - endNode.x) + Math.abs(startNode.y - endNode.y)
        :
        -1;
}

/**
 * 查找邻居点
 *
 * 返回邻居点数组
 */
AStarPathFinding.prototype.getNeighbors = function (node) {
    let arr = [],
        x, y;

    for (let i = -1; i < 2; i++) {
        for (let j = -1; j < 2; j++) {
            if ((i == 0 && j == 0) || (i === j) || (i === -j)) continue;
            x = node.x + i;
            y = node.y + j;
            if (x < this.W && x > -1 && y < this.H && y > -1) {
                arr.push(new Node(x, y));
            }
        }
    }

    return arr;
}

function isBlock(val,blocks){
    for(let i = 0 ; i < blocks.length ; i++){
        if(blocks[i] === val){
            return true;
        }
    }
    return false 
}

/**
 * 查找路径
 *
 * 1 初始化起始点，计算g，h
 * 2 将起始点加入到OpenSet中
 * 3 当OpenSet不为空的时候，进入循环
 * 3.1 从OpenSet中取出最小点，设为当前点
 * 3.2 循环遍历当前点的邻居点
 * 3.2.1 如果邻居点不可通过（为墙壁）或者已经在CloseSet中，就略过continue
 * 3.2.2 如果不在OpenSet中，计算FGH数值，并加入到CloseSet的尾部
 * 3.3 循环遍历邻居点结束
 * 4 OpenSet循环结束
 */
AStarPathFinding.prototype.findPath = function (startNode, endNode , blocks) {
    let OpenSet = new heapMin(),
        CloseSet = new Set(),
        curNode = null;

    OpenSet.push(startNode);

    // 循环遍历OpenSet直到为空
    while (!OpenSet.empty()) {
        curNode = OpenSet.pop();
        CloseSet.push(curNode);

        if (curNode.x === endNode.x && curNode.y === endNode.y) { return CloseSet.set; }

        let arr = this.getNeighbors(curNode);

        for (let i = arr.length - 1; i >= 0; i--) {
            if ( isBlock(this.map[arr[i].y][arr[i].x],blocks) || CloseSet.contain(arr[i])) continue;

            // 不存在，加入到OpenSet集合
            if (!OpenSet.contain(arr[i])) {
                arr[i].g = calcDistance(arr[i], startNode);
                arr[i].h = calcDistance(arr[i], endNode);
                // 更新父节点，便于之后路径查找
                arr[i].p = curNode;
                OpenSet.push(arr[i]);
            }

        }

    }

    return null;
}

/**
 * 打印寻路结果地图
 */
AStarPathFinding.prototype.printMap = function (s, e,blocks) {
    if (s.x < 0 || s.x > this.W - 1 || s.y < 0 || s.y > this.H - 1 || e.x < 0 || e.x > this.W - 1 || e.y < 0 || e.y > this.H - 1)
        return;
    let arr = this.findPath(s, e,blocks);
    console.log(6666)
    console.log(arr)
    if (arr == null) {
        console.log('Not found Path...');
        return;
    }

    let map = this.map.slice();
    let node = arr.pop();
    
    while (node !== null) {
        map[node.y][node.x] = ((s.x === node.x && s.y === node.y) || (e.x === node.x && e.y === node.y)) ? '+' : '*';
        node = node.p;
    }

    for (let i = 0; i < this.H; i++) {
        let temp = [];
        for (let j = 0; j < this.W; j++) {
            temp[j] = map[i][j];
        }
        //document.write(temp.join(' ') + '<br />');
    }

}



/**
 * 获取寻路结果
 */
 AStarPathFinding.prototype.getPath = function (s, e,blocks) {
    if (s.x < 0 || s.x > this.W - 1 || s.y < 0 || s.y > this.H - 1 || e.x < 0 || e.x > this.W - 1 || e.y < 0 || e.y > this.H - 1)
        return;
    let arr = this.findPath(s, e,blocks);
    // console.log(arr)
    if (arr == null) {
        console.log('Not found Path...');
        return;
    }

    let map = this.map.slice();
    let node = arr.pop();

	// console.log(node);
    // console.log(map)
	
	let path = [];

    while (node !== null) {
		path.unshift(node);
        node = node.p;
    }
	
	return path;

}


export default {Node, AStarPathFinding}

// // 定义地图数组 0表示可以通过，1表示不可以通过
// let map = [//0 1 2 3 4 5 6 7
//     [0, 0, 0, 0, 1, 0, 0, 0],//0
//     [0, 0, 1, 1, 1, 0, 0, 0],//1
//     [0, 0, 0, 0, 1, 0, 0, 0],//2
//     [1, 1, 1, 0, 0, 0, 0, 0],//3
//     [0, 0, 0, 0, 1, 0, 0, 0],//4
//     [0, 0, 1, 1, 1, 0, 0, 0],//5
//     [0, 0, 1, 0, 0, 0, 0, 0],//6
//     [0, 0, 0, 0, 0, 0, 0, 0],//7
// ];

// let AStarPathFindingObj = new AStarPathFinding(8, 8, map);

// AStarPathFindingObj.printMap(new Node(0, 0), new Node(6, 4));