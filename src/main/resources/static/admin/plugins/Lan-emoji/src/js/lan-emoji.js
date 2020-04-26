var emoji = function(option) {
  _insertAtCursor = function(emoji_code, option, uuid) {
    const editor = option.editor
    const alias = option.icon[0].alias
    const path = option.icon[0].path
    const key = Object.keys(alias).find(key => alias[key] === emoji_code)
    const source = `<img src="${path}/${key}.jpg" class="editor_img_${uuid}" data-emoji_code="${emoji_code}">`

    document.getElementById(editor).focus()
    var selection = window.getSelection
      ? window.getSelection()
      : document.selection
    var range = selection.createRange
      ? selection.createRange()
      : selection.getRangeAt(0)
    if (!window.getSelection) {
      var selection = window.getSelection
        ? window.getSelection()
        : document.selection
      var range = selection.createRange
        ? selection.createRange()
        : selection.getRangeAt(0)
      range.pasteHTML(source)
      range.collapse(false)
      range.select()
    } else {
      range.collapse(false)
      var hasR = range.createContextualFragment(source)
      var hasR_lastChild = hasR.lastChild
      while (
        hasR_lastChild &&
        hasR_lastChild.nodeName.toLowerCase() == 'br' &&
        hasR_lastChild.previousSibling &&
        hasR_lastChild.previousSibling.nodeName.toLowerCase() == 'br'
      ) {
        var e = hasR_lastChild
        hasR_lastChild = hasR_lastChild.previousSibling
        hasR.removeChild(e)
      }
      range.insertNode(hasR)
      if (hasR_lastChild) {
        range.setEndAfter(hasR_lastChild)
        range.setStartAfter(hasR_lastChild)
      }
      selection.removeAllRanges()
      selection.addRange(range)
    }
  }

  _target = function(isShow) {
    const _emoji = document.getElementById('emoji')
    const toggle = isShow === void 0 ? _emoji.className === 'box-close' : isShow

    if (toggle) {
      _emoji.classList.remove('box-close')
      _emoji.classList.add('box-open')
    } else {
      _emoji.classList.remove('box-open')
      _emoji.classList.add('box-close')
    }
  }

  _uuid = function() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = (Math.random() * 16) | 0,
        v = c == 'x' ? r : (r & 0x3) | 0x8
      return v.toString(16)
    })
  }

  const uuid = _uuid()
  const editor = option.editor
  const btn = option.btn
  this.option = option
  this.uuid = uuid
  this.target = _target

  if (editor) {
    let dom = ''
    const size = Object.keys(option.icon[0].alias).length
    const path = option.icon[0].path

    for (i = 1; i < size; i++) {
      dom += `
    <div class="emoji_content" data-emoji_code="${option.icon[0].alias[i]}">
      <img src="${path}/${i}.jpg">
    </div>
    `
    }
    const _emoji = document.getElementById('emoji')

    _emoji.classList.add('box-close')
    _emoji.innerHTML = dom

    const _emoji_content = document.getElementsByClassName('emoji_content')
    const _emoji_button = document.getElementById(btn)

    for (let i = 0; i < _emoji_content.length; i++) {
      _emoji_content[i].addEventListener(
        'click',
        function() {
          const emoji_code = this.dataset.emoji_code
          _insertAtCursor(emoji_code, option, uuid)
        },
        false
      )
    }

    _emoji_button.addEventListener(
      'click',
      function() {
        _target()
      },
      false
    )
  }
}

emoji.prototype.emojiParse = function(dom) {
  const alias = this.option.icon[0].alias
  const path = option.icon[0].path
  const uuid = this.uuid
  let revertAlias = {}

  for (var attr in alias) {
    if (alias.hasOwnProperty(attr)) {
      revertAlias[alias[attr]] = attr
    }
  }
  for (let index = 0; index < dom.length; index++) {
    const innerHTML = dom[index].innerHTML
    const replace = innerHTML.replace(/:([\s\S]+?):/g, function($0, $1) {
      var n = revertAlias[$1]
      if (n) {
        return `<img src="${path}/${n}.jpg" class="editor_img_${uuid}" data-emoji_code="${$1}">`
      } else {
        return $0
      }
    })
    dom[index].innerHTML = replace
  }
}

emoji.prototype.emojiChange = function() {
  const uuid = this.uuid
  const emojiImage = document.querySelectorAll(`.editor_img_${uuid}`)

  for (let index = 0; index < emojiImage.length; index++) {
    const emoji_code = emojiImage[index].dataset.emoji_code
    emojiImage[index].outerHTML = `:${emoji_code}:`
  }
}

emoji.prototype.toggle = function(isShow) {
  this.target(isShow)
}
