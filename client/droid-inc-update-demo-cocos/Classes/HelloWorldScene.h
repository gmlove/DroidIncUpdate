#ifndef __HELLOWORLD_SCENE_H__
#define __HELLOWORLD_SCENE_H__

#include "cocos2d.h"


using namespace cocos2d;

class HelloWorld : public cocos2d::CCLayer
{
    cocos2d::CCDictionary* _sprites;
    CCSprite* mBird;
    CCSprite* mLand;
    CCJumpBy* mBirdJumpBy;
    CCLayer* mPipeLayer;
    CCLayer* mPipeLayer2;
    CCNode* mMenu;
    CCNode* mPipe1up;
    CCLabelTTF* scoreLabel;
    int mScore;
    bool mGameOver;
    bool mGameStarted;
    bool mPlayPassBy;
    bool mPlayPassBy2;
    
public:
    HelloWorld();
    ~HelloWorld();
    // Here's a difference. Method 'init' in cocos2d-x returns bool, instead of returning 'id' in cocos2d-iphone
    virtual bool init();

    // there's no 'id' in cpp, so we recommend returning the class instance pointer
    static cocos2d::CCScene* scene();
    
    // a selector callback
    void menuCloseCallback(CCObject* pSender);
    void menuStartCallback(CCObject* pSender);
    
    void landMoveFinished(CCNode* sender);
    void pipeMoveFinished(CCNode* sender);
    void ccTouchesBegan(CCSet* touches, CCEvent* event);
    void update();
    void startGame();
    void gameOver();
    // implement the "static node()" method manually
    CREATE_FUNC(HelloWorld);

protected:
    void _initBird();
    void _initLand();
    void _initPipe();
    void __initPipe(CCLayer* pipeLayer);
    void _initPipe1();
    void _initPipe2();
    void _initBtn();
    void _initCloseBtn();
    bool _collision(CCNode* pipeLayer, CCRect birdBounds);
    void _passBy();
    CCAction* _createLandAct();
    CCAction* _createBirdJump();
    CCAction* _createPipeAct();
    CCLabelTTF* _createScoreLabel();
    CCJumpBy* _getMBirdJumpBy();
};

#endif // __HELLOWORLD_SCENE_H__
