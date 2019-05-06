let jsonData;
let ctx;
let htmlBuffer = [];
let checked;

export function afterLoad() {
  let data = JSON.parse((document.getElementById('input') as HTMLInputElement).value);
  jsonData = data;
  buildRoot(data);
  document.getElementById('dependencyTree').innerHTML = htmlBuffer.join('');
  checked = (document.getElementById('showAllDependencies') as HTMLInputElement).checked;
  ctx = (document.getElementById('canvas') as HTMLCanvasElement).getContext('2d');

  let toggler = document.getElementsByClassName('clickable');
  for (let i = 0; i < toggler.length; i++) {
    toggler[i].addEventListener('click', () => {
      toggle(toggler[i]);
      ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
      loadDependencies(data);
    });
  }
  document.getElementById('showAllDependencies').addEventListener('change', () => {
    checked = (document.getElementById('showAllDependencies') as HTMLInputElement).checked;
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    loadDependencies(data);
  });
  ctx.canvas.height = document.getElementById('dependencyTree').offsetHeight;
  ctx.canvas.width = document.getElementById('dependencyTree').offsetWidth;
  loadDependencies(data);
}

function loadDependencies(node) {
  if (node.dependencies.length > 0 && node.children.length === 0) {
    listDependencies(node, ctx);
  }
  if (node.children.length > 0) {
    for (let child of node.children) {
      loadDependencies(child);
    }
  }
}

function buildRoot(currentNode) {
  htmlBuffer.push(`<table class="list list__root active">`);
  htmlBuffer.push(`<tr><td class="package package__base">` +
    `<span id="${currentNode.packageName}" ${currentNode.children.length > 0 ? 'class="clickable"' : ''}>${currentNode.filename}</span>`);

  if (currentNode.children.length > 0) {
    htmlBuffer.push(`<table class="list nested">`);
    htmlBuffer.push(buildTree(currentNode));
    htmlBuffer.push('</table>');
  }
  htmlBuffer.push(`</td></tr></table>`);
}

function buildTree(currentNode) {
  let layer = -1;
  htmlBuffer.push('<tr style="display: none">');
  for (const child of currentNode.children) {
    let classString;
    if (child.children.length > 0) {
      classString = 'package';
    } else if (child.dependencies.length > 0) {
      classString = 'class--dependency';
    } else if (child.children.length === 0 && child.dependencies.length === 0) {
      classString = 'class';
    }
    if (layer === child.layer) {
      // add in same row as child
      htmlBuffer.push(`<td class="${classString}">` +
        `<span id="${child.packageName}" ${child.children.length > 0 ? 'class="clickable"' : ''}>${child.filename}</span>`);
    } else {
      // create new row
      htmlBuffer.push(`</tr><tr><td class="${classString}">` +
        `<span id="${child.packageName}" ${child.children.length > 0 ? 'class="clickable"' : ''}>${child.filename}</span>`);
    }
    layer = child.layer;
    if (child.children.length > 0) {
      htmlBuffer.push(`<table class="list nested">`);
      htmlBuffer.push(buildTree(child));
      htmlBuffer.push('</table>');
    }
    htmlBuffer.push('</td>')
  }
  htmlBuffer.push(`</tr>`);
}

function toggle(currentNode) {
  if (currentNode.nextSibling.classList.contains('nested')) {
    currentNode.nextSibling.classList.remove('nested');
    if (currentNode.nextSibling.classList.contains('list__root')) {
      currentNode.nextSibling.classList.add('active__root');
    } else {
      currentNode.nextSibling.classList.add('active');
    }
  } else if (currentNode.nextSibling.classList.contains('active')) {
    collapseChildren(currentNode);
    currentNode.nextSibling.classList.add('nested');
    if (currentNode.nextSibling.classList.contains('list__root')) {
      currentNode.nextSibling.classList.remove('active__root');
    } else {
      currentNode.nextSibling.classList.remove('active');
    }
  }
  // set height of canvas to the height of dependencyTree after toggle
  ctx.canvas.height = document.getElementById('dependencyTree').offsetHeight;
  ctx.canvas.width = document.getElementById('dependencyTree').offsetWidth;
}

function listDependencies(currentNode, ctx) {
  ctx.lineWidth = 1;
  // draw arrows to my dependencies and call this function for my dependencies
  if (currentNode.dependencies.length > 0) {
    currentNode.dependencies.forEach(dependency => {
      // find last visible element for dependency as end
      const end = findLastHTMLElement(dependency).parentNode;
      // find last visible element for currentNode as start
      let start = findLastHTMLElement(currentNode).parentNode;

      let startx = 0, starty = 0, endx = 0, endy = 0;
      // calculate offsets across all parents for start and end
      let tmp = start;
      do {
        startx += tmp.offsetLeft;
        starty += tmp.offsetTop;
        tmp = tmp.offsetParent;
      } while (!tmp.classList.contains('list__root'));
      tmp = end;
      do {
        endx += tmp.offsetLeft;
        endy += tmp.offsetTop;
        tmp = tmp.offsetParent;
      } while (!tmp.classList.contains('list__root'));

      // check if all dependencies or only 'circular' dependencies should be shown
      if (!checked) {
        // check if current dependency is 'circular'
        if (starty >= endy) {
          canvasArrow(ctx, startx, starty + start.offsetHeight / 2, endx + end.offsetWidth, endy + end.offsetHeight / 2);
        }
      } else {
        canvasArrow(ctx, startx, starty + start.offsetHeight / 2, endx + end.offsetWidth, endy + end.offsetHeight / 2);
      }
    });
  }
}

function findLastHTMLElement(node) {
  let packageName = node.packageName;
  let element;
  while (element === undefined) {
    if (!document.getElementById(packageName) || document.getElementById(packageName).offsetParent === null) {
      packageName = packageName.substring(0, packageName.lastIndexOf('.'));
    } else {
      element = document.getElementById(packageName);
    }
  }
  return element;
}

function canvasArrow(context, fromx, fromy, tox, toy) {
  const headlen = 10;

  // draw curved line
  context.beginPath();
  context.setLineDash([10]);
  context.moveTo(fromx, fromy);
  // span right triangle with X, Y and Z with X = (fromx, fromy) and Y = (tox, toy) and Z as the point at the right angle
  // calculate all sides x, y as the sides leading to the right angle
  let x = Math.abs(fromx - tox), y = Math.abs(fromy - toy);
  // calculate z with (zx, zy)
  let zx, zy;
  if (fromx <= tox && fromy <= toy) {
    zx = Math.max(fromx, tox)-x;
    zy = Math.max(fromy, toy);
  } else if (fromx <= tox && fromy > toy) {
    zx = Math.max(fromx, tox);
    zy = Math.min(fromy, toy)+y;
  } else if (fromx > tox && fromy <= toy) {
    zx = Math.min(fromx, tox)+x;
    zy = Math.max(fromy, toy);
  } else if (fromx > tox && fromy > toy) {
    zx = Math.min(fromx, tox);
    zy = Math.min(fromy, toy)+y;
  }

  // draw quadratic curve from X over Z to Y
  context.quadraticCurveTo(zx, zy, tox, toy);
  context.stroke();

  // calculate angle for arrow head in relation to line
  let angle = Math.atan2(toy - zy, tox - zx);
  // draw arrow head
  context.beginPath();
  context.moveTo(tox, toy);
  context.setLineDash([0]);
  context.lineTo(tox - headlen * Math.cos(angle - Math.PI / 6), toy - headlen * Math.sin(angle - Math.PI / 6));
  context.moveTo(tox, toy);
  context.lineTo(tox - headlen * Math.cos(angle + Math.PI / 6), toy - headlen * Math.sin(angle + Math.PI / 6));
  context.stroke();
}

function collapseChildren(currentNode) {
  if (currentNode.nextSibling) {
    let toCollapse = currentNode.nextSibling.getElementsByClassName('clickable');
    for (let i = 0; i < toCollapse.length; i++) {
      if (toCollapse[i].nextSibling) {
        toCollapse[i].nextSibling.classList.add('nested');
        toCollapse[i].nextSibling.classList.remove('active');
      }
    }
  }
}
