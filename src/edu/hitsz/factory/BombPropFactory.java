package edu.hitsz.factory;

import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropBomb;

public class BombPropFactory implements PropFactory {

    // 使用无参构造函数
    public BombPropFactory() {

    }

    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        // 创建基础的PropBomb，不需要传递Game引用
        return new PropBomb(locationX, locationY, 0, 5);
    }
}