#include "HelloWorldScene.h"
#include "SimpleAudioEngine.h"

USING_NS_CC;

static const int PIPE_GAP = 120;
static const int PIPE_MOVE_INTERVAL = 5;

CCScene* HelloWorld::scene()
{
    // 'scene' is an autorelease object
    CCScene *scene = CCScene::create();
    
    // 'layer' is an autorelease object
    HelloWorld *layer = HelloWorld::create();

    // add layer as a child to scene
    scene->addChild(layer);

    // return the scene
    return scene;
}

CCSprite* createSprite(int width, int height, float x, float y)
{
    return CCSprite::create("atlas.png", CCRect(1024 * x, 1024 * y, width, height));
}

int _randPipeYPos()
{
    // must be less than 320
    // 30 is margin-top
    // 112 is height of land
    int ypos = arc4random() % (320 - 112 - 30) + (112 + 30);
    CCLog("random YPos of pipe: %d.", ypos);
    return ypos;
}

CCRect caculateBounds(const CCSize& size, const CCPoint& anchor, const CCPoint& position)
{
    return CCRectMake(position.x - anchor.x * size.width, position.y - anchor.y * size.height, size.width, size.height);
}



CCSpriteFrame* createSpriteFrame(int width, int height, float x, float y)
{
    return CCSpriteFrame::create("atlas.png", CCRect(1024 * x, 1024 * y, width, height));
}

HelloWorld::~HelloWorld()
{
	if (_sprites)
	{
		_sprites->release();
		_sprites = NULL;
	}
    this->removeAllChildren();
    if (mMenu) {
        mMenu->release();
        mMenu = NULL;
    }
    if (scoreLabel) {
        scoreLabel->release();
        scoreLabel = NULL;
    }

	// cpp don't need to call super dealloc
	// virtual destructor will do this
}

HelloWorld::HelloWorld()
:_sprites(NULL),
mBird(NULL),
mPipeLayer(NULL),
mPipeLayer2(NULL),
mGameOver(false),
mPlayPassBy(false),
mPlayPassBy2(false),
mScore(0),
scoreLabel(NULL),
mMenu(NULL),
mGameStarted(false)
{
}

// on "init" you need to initialize your instance
bool HelloWorld::init()
{
    //////////////////////////////
    // 1. super init first
    if ( !CCLayer::init() )
    {
        return false;
    }
    
    CCSize visibleSize = CCDirector::sharedDirector()->getVisibleSize();
    CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();

    /////////////////////////////
    // 2. add a menu item with "X" image, which is clicked to quit the program
    //    you may modify it.

    // add a "close" icon to exit the progress. it's an autorelease object
    CCMenuItemImage *pCloseItem = CCMenuItemImage::create(
                                        "CloseNormal.png",
                                        "CloseSelected.png",
                                        this,
                                        menu_selector(HelloWorld::menuCloseCallback));
    
	pCloseItem->setPosition(ccp(origin.x + visibleSize.width - pCloseItem->getContentSize().width/2 ,
                                origin.y + pCloseItem->getContentSize().height/2));

    // create menu, it's an autorelease object
    CCMenu* pMenu = CCMenu::create(pCloseItem, NULL);
    pMenu->setPosition(CCPointZero);
    this->addChild(pMenu, 40);

    /////////////////////////////
    // 3. add your codes below...
    this->_createScoreLabel();
    this->_sprites = CCDictionary::create();
    
    CCSprite* bg_day = CCSprite::create("atlas.png", CCRect(0, 0, 288, 512));
    bg_day->setPosition(ccp(0,0));
    bg_day->setAnchorPoint(ccp(0,0));
    this->addChild(bg_day, 0);
    this->_sprites->setObject(bg_day, "bg_day");
    
    this->_initLand();
    this->_initBtn();
    this->setTouchEnabled(true);
    
    CocosDenshion::SimpleAudioEngine::sharedEngine()->preloadBackgroundMusic("sfx_die.wav");
    CocosDenshion::SimpleAudioEngine::sharedEngine()->preloadBackgroundMusic("sfx_hit.wav");
    CocosDenshion::SimpleAudioEngine::sharedEngine()->preloadBackgroundMusic("sfx_point.wav");
    CocosDenshion::SimpleAudioEngine::sharedEngine()->preloadBackgroundMusic("sfx_swooshing.wav");
    CocosDenshion::SimpleAudioEngine::sharedEngine()->preloadBackgroundMusic("sfx_wing.wav");
    return true;
}

void HelloWorld::_initPipe2()
{
    if (!mPipeLayer2) {
        mPipeLayer2 = CCLayer::create();
        this->__initPipe(mPipeLayer2);
    } else {
        mPipeLayer2->runAction(this->_createPipeAct());
    }
}

void HelloWorld::_initPipe1()
{
    if (!mPipeLayer) {
        mPipeLayer = CCLayer::create();
        this->__initPipe(mPipeLayer);
    } else {
        mPipeLayer->runAction(this->_createPipeAct());
    }
}

void HelloWorld::__initPipe(CCLayer* pipeLayer)
{
    pipeLayer->setAnchorPoint(ccp(0, 0));
    pipeLayer->setPosition(ccp(288, 0));
    
    CCSprite* pipe1up = createSprite(52, 320, 0.1640625, 0.6308594);
    pipe1up->setAnchorPoint(ccp(0, 1));
    pipe1up->setTag(1);
    int ypos = _randPipeYPos();
    int xpos = 0;
    pipe1up->setPosition(ccp(xpos, ypos));
    mPipe1up = pipe1up;
    pipeLayer->addChild(pipe1up);
    
    CCSprite* pipe1down = createSprite(52, 320, 0.109375, 0.6308594);
    pipe1down->setAnchorPoint(ccp(0, 0));
    pipe1down->setPosition(ccp(xpos, ypos + PIPE_GAP));
    pipe1down->setTag(2);
    pipeLayer->addChild(pipe1down);
    
    pipeLayer->runAction(this->_createPipeAct());
    CCLog("getbytag1: %d, %d", pipeLayer->getChildByTag(1), pipeLayer);
    CCLog("getbytag1.0: %d, %d", mPipeLayer->getChildByTag(1), mPipeLayer);
    this->addChild(pipeLayer, 10);
}

void HelloWorld::_initPipe()
{
    if (mGameStarted) {
        if (mPipeLayer) {
            mPipeLayer->stopAllActions();
            mPipeLayer->setPosition(ccp(288, 0));
        }
        if (mPipeLayer2) {
            mPipeLayer2->stopAllActions();
            mPipeLayer2->setPosition(ccp(288, 0));
        }
        this->unschedule(schedule_selector(HelloWorld::_initPipe2));
    }
    this->_initPipe1();
    this->scheduleOnce(schedule_selector(HelloWorld::_initPipe2), PIPE_MOVE_INTERVAL / 2.0);
}

CCAction* HelloWorld::_createPipeAct()
{
    CCMoveBy* pipeMove = CCMoveBy::create(PIPE_MOVE_INTERVAL, ccp(-288 - 52, 0));
    CCFiniteTimeAction* pipeMoveDone = CCCallFuncN::create(this,
                                                           callfuncN_selector(HelloWorld::pipeMoveFinished));
    return CCSequence::createWithTwoActions(pipeMove, pipeMoveDone);
}

void HelloWorld::pipeMoveFinished(CCNode *sender)
{
   	CCLayer *pipe = (CCLayer *)sender;
    int ypos = _randPipeYPos();
    int xpos = 0;
    pipe->getChildByTag(1)->setPosition(ccp(xpos, ypos));
    pipe->getChildByTag(2)->setPosition(ccp(xpos, ypos + PIPE_GAP));
    pipe->setPosition(ccp(288, 0));
    pipe->runAction(this->_createPipeAct());
}

void HelloWorld::_initLand()
{
    mLand = createSprite(336, 112, 0.5703125, 0);
    mLand->setAnchorPoint(ccp(0, 0));
    mLand->setPosition(ccp(0, 0));

	mLand->runAction(this->_createLandAct());
    this->addChild(mLand, 20);
    this->_sprites->setObject(mLand, "land");
}

void HelloWorld::_initBird()
{
    CCSize size = CCDirector::sharedDirector()->getWinSize();
    if (mGameStarted) {
        mBird->stopAllActions();
        mBird->setPosition(ccp(size.width/3, size.height/2));
        mBird->setRotation(0);
        mBird->runAction(this->_createBirdJump());
        return;
    }
    mBird = createSprite(48, 48, 0.1640625, 0.9472656);
    mBird->setPosition(ccp(size.width/3, size.height/2));
    mBird->runAction(this->_createBirdJump());
    this->addChild(mBird, 30);
}

CCAction* HelloWorld::_createBirdJump()
{
    CCJumpBy* birdJumpBy = this->_getMBirdJumpBy();
    CCAnimation* birdFly = CCAnimation::create();
    birdFly->addSpriteFrame(createSpriteFrame(48, 48, 0.1640625, 0.9472656));
    birdFly->addSpriteFrame(createSpriteFrame(48, 48, 0.21875, 0.6308594));
    birdFly->addSpriteFrame(createSpriteFrame(48, 48, 0.21875, 0.6816406));
    birdFly->setDelayPerUnit(0.05);
    birdFly->setLoops(-1);
    birdFly->setRestoreOriginalFrame(true);
    CCAnimate* birdFlyAnimate = CCAnimate::create(birdFly);
    
    CCRotateBy* birdRotateUp = CCRotateBy::create(0.1, -30);
    CCRotateBy* birdRotateStill = CCRotateBy::create(0.4, 0);
    CCRotateBy* birdRotateDown = CCRotateBy::create(0.5, 120);
    CCSequence* birdRotate = CCSequence::create(birdRotateUp, birdRotateStill, birdRotateDown, NULL);
    
    CCAction* birdJump = CCSpawn::create(birdJumpBy, birdFlyAnimate, birdRotate, NULL);
    return birdJump;
}

void HelloWorld::_initBtn()
{
    CCSize visibleSize = CCDirector::sharedDirector()->getVisibleSize();
    CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();
    CCSprite* start = createSprite(116, 70, 0.6855469, 0.22851562);
    CCMenuItemSprite *btn = CCMenuItemSprite::create(start, start, this, menu_selector(HelloWorld::menuStartCallback));
    btn->setAnchorPoint(ccp(0.5, 0.5));
	btn->setPosition(ccp(visibleSize.width/2, visibleSize.height/2));
    
    // create menu, it's an autorelease object
    mMenu = CCMenu::create(btn, NULL);
    mMenu->retain();
    mMenu->setPosition(CCPointZero);
    this->addChild(mMenu, 40);
}

void HelloWorld::menuStartCallback(cocos2d::CCObject *pSender)
{
    this->removeChild(mMenu);
    this->scheduleOnce(schedule_selector(HelloWorld::startGame), 0.2);
}

void HelloWorld::startGame()
{
    this->_initBird();
    this->_initPipe();
    if (!mGameStarted) {
        this->schedule(schedule_selector(HelloWorld::update), 0.0f, kCCRepeatForever, 0.0f);
    }
    mGameStarted = true;
    mGameOver = false;
}

void HelloWorld::update()
{
    if (mBird && !mGameOver) {
        CCRect birdBounds = caculateBounds(mBird->getContentSize(), mBird->getAnchorPoint(), mBird->getPosition());
        CCLog("bird rounds: x=%f, y=%f, w=%f, h=%f", birdBounds.origin.x, birdBounds.origin.y, birdBounds.size.width, birdBounds.size.height);
        do {
            if (this->_collision(mPipeLayer, birdBounds)) {
                mGameOver = true;
                break;
            }
            if (this->_collision(mPipeLayer2, birdBounds)) {
                mGameOver = true;
                break;
            }
            if (birdBounds.intersectsRect(caculateBounds(mLand->getContentSize(),mLand->getAnchorPoint(), mLand->getPosition()))) {
                CCLog("collision with land!");
                mGameOver = true;
                break;
            }
            this->_passBy();
            mGameOver = false;
        } while (0);
        if (mGameOver) {
            this->gameOver();
        }
    }
}

bool HelloWorld::_collision(CCNode *pipeLayer, CCRect birdBounds)
{
    if (!pipeLayer) {
        return false;
    }
    CCArray* pipes = pipeLayer->getChildren();
    CCObject* pipeObj = NULL;
    CCARRAY_FOREACH(pipes, pipeObj){
        CCNode* pipeNode = (CCNode*)(pipeObj);
        CCPoint opos = pipeNode->getPosition();
        CCPoint wpos = pipeLayer->convertToWorldSpace(opos);
        if (pipeNode->getTag() == 1) {
            wpos.y -= 15;
        } else {
            wpos.y += 15;
        }
        CCRect pipe1Bounds = caculateBounds(pipeNode->getContentSize(), pipeNode->getAnchorPoint(), wpos);
        CCLog("pipe(tag=%d) rounds: x=%f, y=%f, w=%f, h=%f", pipeNode->getTag(), pipe1Bounds.origin.x, pipe1Bounds.origin.y, pipe1Bounds.size.width, pipe1Bounds.size.height);
        if (birdBounds.intersectsRect(pipe1Bounds)) {
            CCLog("collision detected!");
            return true;
        }
    }
    return false;
}

void HelloWorld::_passBy()
{
    if (mGameOver) {
        return;
    }
    float birdXPos = mBird->getPosition().x;
    if (mPipeLayer) {
        CCNode* pipeup = mPipeLayer->getChildByTag(1);
        CCPoint wpos = mPipeLayer->convertToWorldSpace(pipeup->getPosition());
        if (birdXPos - (wpos.x + 52) < 10 && birdXPos - (wpos.x + 52) > 0) {
            if (!mPlayPassBy) {
                CocosDenshion::SimpleAudioEngine::sharedEngine()->playEffect("sfx_point.wav");
                this->_createScoreLabel();
            }
            mPlayPassBy = true;
        } else {
            mPlayPassBy = false;
        }
    }
    if (mPipeLayer2) {
        CCNode* pipeup = mPipeLayer2->getChildByTag(1);
        CCPoint wpos = mPipeLayer2->convertToWorldSpace(pipeup->getPosition());
        if (birdXPos - (wpos.x + 52) < 10 && birdXPos - (wpos.x + 52) > 0) {
            if (!mPlayPassBy2) {
                CocosDenshion::SimpleAudioEngine::sharedEngine()->playEffect("sfx_point.wav");
                this->_createScoreLabel();
            }
            mPlayPassBy2 = true;
        } else {
            mPlayPassBy2 = false;
        }
    }
}

CCLabelTTF* HelloWorld::_createScoreLabel()
{
    CCSize visibleSize = CCDirector::sharedDirector()->getVisibleSize();
    CCPoint origin = CCDirector::sharedDirector()->getVisibleOrigin();
    if (scoreLabel) {
        this->removeChild(scoreLabel);
    }
    const char* scoreStr = (CCString::createWithFormat("Your Score: %d", mScore++))->getCString();
    scoreLabel = CCLabelTTF::create(scoreStr, "Arial", 24);
    
    // position the label on the center of the screen
    scoreLabel->setPosition(ccp(origin.x + visibleSize.width/2,
                            origin.y + visibleSize.height - scoreLabel->getContentSize().height));
    
    // add the label as a child to this layer
    this->addChild(scoreLabel, 40);
}

void HelloWorld::gameOver()
{
    CocosDenshion::SimpleAudioEngine::sharedEngine()->playEffect("sfx_hit.wav");
    mBird->stopAllActions();
    mBird->setRotation(-45);
    CCMoveBy* moveDown = CCMoveBy::create(0.5, ccp(0, -mBird->getPosition().y + 112));
    CCRotateTo* rotateDown = CCRotateTo::create(0.2, 90);
    mBird->runAction(CCSpawn::createWithTwoActions(moveDown, rotateDown));
    this->addChild(mMenu, 40);
}

CCJumpBy* HelloWorld::_getMBirdJumpBy()
{
    return CCJumpBy::create(1.2, ccp(0, -(512 - 112)), 180, 1);
}

void HelloWorld::ccTouchesBegan(CCSet* touches, CCEvent* event)
{
    if (mGameOver || !mGameStarted) {
        return;
    }
    CCTouch* touch = (CCTouch*)( touches->anyObject() );
	CCPoint location = touch->getLocation();
    
    mBird->stopAllActions();
    mBird->setRotation(0);
    mBird->runAction(this->_createBirdJump());
    CocosDenshion::SimpleAudioEngine::sharedEngine()->playEffect("sfx_wing.wav");
}

CCAction* HelloWorld::_createLandAct()
{
    CCFiniteTimeAction* landMove = CCMoveTo::create(0.5, ccp(288 - 336, 0));
    CCFiniteTimeAction* landMoveDone = CCCallFuncN::create(this,
                                                           callfuncN_selector(HelloWorld::landMoveFinished));
    return CCSequence::create(landMove, landMoveDone, NULL);
}

void HelloWorld::landMoveFinished(CCNode* sender)
{
	CCSprite *land = (CCSprite *)sender;
    land->setPosition(ccp(0, 0));
	land->runAction(this->_createLandAct());
}

void HelloWorld::menuCloseCallback(CCObject* pSender)
{
#if (CC_TARGET_PLATFORM == CC_PLATFORM_WINRT) || (CC_TARGET_PLATFORM == CC_PLATFORM_WP8)
	CCMessageBox("You pressed the close button. Windows Store Apps do not implement a close button.","Alert");
#else
    CCDirector::sharedDirector()->end();
#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    exit(0);
#endif
#endif
}
