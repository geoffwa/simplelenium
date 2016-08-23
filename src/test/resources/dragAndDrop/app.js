var dnd;

dnd = angular.module('dnd', ['ngDraggable']).controller('DndController', (function() {
  function _Class() {
    console.log('Init');
    this.list1 = ['Bob', 'John', 'Jane'];
    this.list2 = [];
  }

  _Class.prototype.onDragList1 = function() {
    return console.log('Drag from list1');
  };

  _Class.prototype.onDragList2 = function() {
    return console.log('Drag from list2');
  };

  _Class.prototype.onEnterList2 = function() {
    return console.log('Enter list2');
  };

  _Class.prototype.onDropList1 = function(data) {
    if (data == null) {
      return;
    }
    console.log('Drop on list1');
    this.removeFromLists(data);
    return this.list1.push(data);
  };

  _Class.prototype.onDropList2 = function(data) {
    if (data == null) {
      return;
    }
    console.log('Drop on list2');
    this.removeFromLists(data);
    return this.list2.push(data);
  };

  _Class.prototype.removeFromLists = function(data) {
    var item;
    this.list2 = (function() {
      var i, len, ref, results;
      ref = this.list2;
      results = [];
      for (i = 0, len = ref.length; i < len; i++) {
        item = ref[i];
        if (item !== data) {
          results.push(item);
        }
      }
      return results;
    }).call(this);
    return this.list1 = (function() {
      var i, len, ref, results;
      ref = this.list1;
      results = [];
      for (i = 0, len = ref.length; i < len; i++) {
        item = ref[i];
        if (item !== data) {
          results.push(item);
        }
      }
      return results;
    }).call(this);
  };

  return _Class;

})());