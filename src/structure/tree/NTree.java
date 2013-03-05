package structure.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 2/21/13
 * Time: 10:03 AM
 */
public class NTree<T> implements NTreeBasic<Collidable<T>> {
    protected final LinkedList<BlackNode> cachedBlackNodes = new LinkedList<BlackNode>();
    protected final LinkedList<WhiteNode> cachedWhiteNodes = new LinkedList<WhiteNode>();
    protected final LinkedList<GrayNode> cachedGrayNodes = new LinkedList<GrayNode>();
    protected final LinkedList<Collidable<T>> cachedCollidables = new LinkedList<Collidable<T>>();

    protected Node getBlackNode(Collidable<T> c) {
        if(!cachedBlackNodes.isEmpty()) {
            Node ret = cachedBlackNodes.pop();
            ret.collidable = c;
            return ret;
        }
        return new BlackNode(c);
    }

    protected Node getWhiteNode(Collidable<T> c) {
        if(!cachedWhiteNodes.isEmpty()) {
            Node ret = cachedWhiteNodes.pop();
            ret.collidable = c;
            return ret;
        }
        return new WhiteNode(c);
    }

    protected Node getGrayNode(Collidable<T> c) {
        if(!cachedGrayNodes.isEmpty()) {
            Node ret = cachedGrayNodes.pop();
            ret.collidable = c;
            return ret;
        }
        return new GrayNode(c);
    }

    protected Collidable<T> getCollidable() {
        if(!cachedCollidables.isEmpty()) {
            return cachedCollidables.pop();
        }
        return null;
    }

    protected abstract class Node {
        protected Collidable<T> collidable;
        public abstract Node add(Collidable<T> t);
        public abstract Node remove(Collidable<T> t);
        public abstract void findIntersecting(Collidable<T> c, List<Collidable<T>> cs);
        public abstract void reset(Collidable<T> c);
    }
    protected class BlackNode extends Node {
        protected ArrayList<Collidable<T>> ts;

        public BlackNode(Collidable<T> collidable) {
            this.collidable = collidable;
            ts = new ArrayList<Collidable<T>>(blackNodeLimit);
        }

        @Override
        public Node add(Collidable<T> t) {
            if(ts.size() == blackNodeLimit) {
                Node ret = new GrayNode(collidable);
                for(Collidable<T> t1 : ts) {
                    ret = ret.add(t1);
                }
                ret = ret.add(t);
                // Make sure the node is in a clean state for when it gets removed from the cache
                cachedBlackNodes.push(this);
                return ret;
            }
            ts.add(t);
            return this;
        }

        @Override
        public Node remove(Collidable<T> t) {
            ts.remove(t);
            if(ts.isEmpty()) {
                cachedBlackNodes.push(this);
                return new WhiteNode(collidable);
            }
            return this;
        }

        @Override
        public void findIntersecting(Collidable<T> c, List<Collidable<T>> cs) {
            for(Collidable<T> myc : ts) {
                if(c.collidesWith(myc)) {
                    cs.add(myc);
                }
            }
        }

        @Override
        public void reset(Collidable<T> c) {
            collidable = c;
            ts.clear();
        }
    }
    protected class WhiteNode extends Node {
        public WhiteNode(Collidable<T> collidable) {
            this.collidable = collidable;
        }

        @Override
        public Node add(Collidable<T> t) {
            System.out.println("Retuning black node from white node");
            Node ret = new BlackNode(collidable);
            cachedWhiteNodes.push(this);
            return ret.add(t);
        }

        @Override
        public Node remove(Collidable<T> t) {
            return this;
        }

        @Override
        public void findIntersecting(Collidable<T> c, List<Collidable<T>> cs) {
        }

        @Override
        public void reset(Collidable<T> c) {
        }
    }
    protected class GrayNode extends Node {
        protected ArrayList<Node> children;

        public GrayNode(Collidable<T> collidable) {
            this.collidable = collidable;
            ArrayList<Collidable<T>> cs = new ArrayList<Collidable<T>>(branches);
            for(int i = 0; i < branches; i++) {
                Collidable<T> c = getCollidable();
                if(c == null) {
                    break;
                }
                cs.add(c);
            }
            divisor.divide(collidable, cs);
            children = new ArrayList<Node>(branches);
            for(int i = 0; i < branches; i++) {
                children.add(getWhiteNode(cs.get(i)));
            }
        }

        @Override
        public Node add(Collidable<T> t) {
            for(int i = 0; i < branches; i++) {
                if(t.collidesWith(children.get(i).collidable)) {
                    children.set(i, children.get(i).add(t));
                }
            }
            return this;
        }

        @Override
        public Node remove(Collidable<T> t) {
            // TODO: Merge black nodes when total children items is < blackNodeLimit
            int blackNodeCount = 0, whiteNodeCount = 0;
            for(int i = 0; i < branches; i++) {
                children.set(i, children.get(i).remove(t));
                if(children.get(i) instanceof NTree<?>.BlackNode) {
                    blackNodeCount++;
                }
                else if(children.get(i) instanceof NTree<?>.WhiteNode) {
                    whiteNodeCount++;
                }
            }
            if(whiteNodeCount == branches) {
                for(int i = 0; i < branches; i++) {
                    Node node = children.get(i);
                    cachedCollidables.push(node.collidable);
                    cachedWhiteNodes.push((WhiteNode)node);
                }
                cachedGrayNodes.push(this);
                return getWhiteNode(collidable);
            }
            else if(blackNodeCount == 1) {
                Node ret = null;
                for(int i = 0; i < branches; i++) {
                    if(children.get(i) instanceof NTree<?>.BlackNode) {
                        ret = children.get(i);
                    }
                    else {
                        cachedWhiteNodes.push((WhiteNode)children.get(i));
                        cachedCollidables.push(children.get(i).collidable);
                    }
                }
                cachedGrayNodes.push(this);
                ret.collidable = collidable;
                return ret;
            }
            return this;
        }

        @Override
        public void findIntersecting(Collidable<T> c, List<Collidable<T>> cs) {
            for(int i = 0; i < branches; i++) {
                Node node = children.get(i);
                if(c.collidesWith(node.collidable)) {
                    node.findIntersecting(c, cs);
                }
            }
        }

        @Override
        public void reset(Collidable<T> collidable) {
            this.collidable = collidable;
            ArrayList<Collidable<T>> cs = new ArrayList<Collidable<T>>(branches);
            for(int i = 0; i < branches; i++) {
                Collidable<T> c = getCollidable();
                if(c == null) {
                    break;
                }
                cs.add(c);
            }
            divisor.divide(collidable, cs);
            for(int i = 0; i < branches; i++) {
//                children[i] = new WhiteNode(cs.get(i));
                children.set(i, getWhiteNode(cs.get(i)));
            }
        }
    }

    protected static final int DEFAULT_LIMIT = 4;
    protected int branches, blackNodeLimit;
    protected Collidable<T> rootCollidable;
    protected CollidableDivisor<T> divisor;
    protected Node rootNode;

    public NTree(int branches, Collidable<T> root, CollidableDivisor<T> divisor) {
        this(branches, DEFAULT_LIMIT, root, divisor);
    }

    public NTree(int branches, int blackNodeLimit, Collidable<T> root, CollidableDivisor<T> divisor) {
        this.branches = branches;
        this.blackNodeLimit = blackNodeLimit;
        this.rootCollidable = root;
        this.divisor = divisor;
        this.rootNode = new WhiteNode(rootCollidable);
    }

    public void add(Collidable<T> c) {
        rootNode = rootNode.add(c);
//        System.out.println("Root node is black: " + (rootNode instanceof NTree<?>.BlackNode));
    }

    @Override
    public void remove(Collidable<T> collidable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Collidable<T>> getIntersecting(Collidable<T> collidable) {
        LinkedList<Collidable<T>> ret = new LinkedList<Collidable<T>>();
        rootNode.findIntersecting(collidable, ret);
        return ret;
    }
}
