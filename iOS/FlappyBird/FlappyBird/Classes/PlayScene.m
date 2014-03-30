//
//  PlayScene.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/14/14.
//  Copyright 2014 Hai Do Minh. All rights reserved.
//

#import "PlayScene.h"
#import "GameConfig.h"
#import "Footer.h"
#import "Bird.h"
#import "CCAnimation.h"

@implementation PlayScene

NSMutableDictionary* atlasInfo;
CCTexture *atlas;

Footer* footer;
CCButton* btnResume;
CCSpriteFrame *btnResumeFrame, *btnPauseFrame;
CCSprite *sprReadyText, *sprTutorial;
Bird* bird;
bool isPause = NO;
NSMutableArray* activePipes;
float REAL_HEIGHT;
int score;
CCLabelTTF *scoreLabel;

float groundY;

+ (PlayScene *)scene {
    return [[self alloc] init];
}
- (id)init {
    self = [super init];
    if (!self) return(nil);
    
    [self setUserInteractionEnabled:YES];
    [self setMultipleTouchEnabled:NO];
    
     atlasInfo = GameConfig.atlasInfo;
     atlas = GameConfig.atlas;
    // background
    int r = arc4random_uniform(2);
    NSString* bgResource = [NSString stringWithFormat:@"bg_%@", ((r == 0) ? @"day" : @"night")];
    NSArray *bginfo = [atlasInfo objectForKey:bgResource];
    CCSprite *background = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[bginfo objectAtIndex:3] intValue], [[bginfo objectAtIndex:4] intValue], [[bginfo objectAtIndex:1] intValue], [[bginfo objectAtIndex:2] intValue])];
    background.position = ccp(self.contentSize.width / 2, self.contentSize.height / 2);
    CGSize ratio = [GameConfig ratio];
    background.scaleX = ratio.width;
    background.scaleY = ratio.height;
    [self addChild:background];
    // tutorial
    NSArray * readyTextInfo = [atlasInfo objectForKey:@"text_ready"];
    NSArray * tutorialInfo = [atlasInfo objectForKey:@"tutorial"];
    CGSize sprTutorialSizeInPixels = CGSizeMake([[tutorialInfo objectAtIndex:1] intValue], [[tutorialInfo objectAtIndex:2] intValue]);
    CGRect btnTutorialRectInPixels = {CGPointMake([[tutorialInfo objectAtIndex:3] intValue], [[tutorialInfo objectAtIndex:4] intValue]), sprTutorialSizeInPixels};
    sprTutorial = [CCSprite spriteWithTexture:atlas rect:btnTutorialRectInPixels];
    CGSize sprReadySizeInPixels = CGSizeMake([[readyTextInfo objectAtIndex:1] intValue], [[readyTextInfo objectAtIndex:2] intValue]);
    CGRect btnReadyRectInPixels = {CGPointMake([[readyTextInfo objectAtIndex:3] intValue], [[readyTextInfo objectAtIndex:4] intValue]), sprReadySizeInPixels};
    sprReadyText = [CCSprite spriteWithTexture:atlas rect:btnReadyRectInPixels];
    sprTutorial.position = ccp(self.contentSize.width / 2, self.contentSize.height / 2);
    sprReadyText.position = ccp(self.contentSize.width / 2, sprTutorial.position.y + sprTutorialSizeInPixels.height / 2 + sprReadySizeInPixels.height / 2);
    [self addChild:sprTutorial];
    [self addChild:sprReadyText];
    // pause/ resume
    NSArray *btnResumeInfo = [atlasInfo objectForKey:@"button_resume"];
    CGSize btnSizeInPixels = CGSizeMake([[btnResumeInfo objectAtIndex:1] intValue], [[btnResumeInfo objectAtIndex:2] intValue]);
    CGRect btnResumeRectInPixels = {CGPointMake([[btnResumeInfo objectAtIndex:3] intValue], [[btnResumeInfo objectAtIndex:4] intValue]), btnSizeInPixels};
    btnResumeFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:btnResumeRectInPixels rotated:NO offset:CGPointZero originalSize:btnSizeInPixels];
    NSArray *btnPauseInfo = [atlasInfo objectForKey:@"button_pause"];
    CGRect btnPauseRectInPixels = {CGPointMake([[btnPauseInfo objectAtIndex:3] intValue], [[btnPauseInfo objectAtIndex:4] intValue]), btnSizeInPixels};
    btnPauseFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:btnPauseRectInPixels rotated:NO offset:CGPointZero originalSize:btnSizeInPixels];
    
    btnResume = [CCButton buttonWithTitle:@"" spriteFrame:btnPauseFrame];
    btnResume.position = ccp(10 + btnResume.contentSize.width / 2, self.contentSize.height - 10 - btnResume.contentSize.height / 2);
    [btnResume setTarget:self selector:@selector(onBtnResumeClicked:)];
    [btnResume setUserObject:@"pause"];
    
    // pipes
    CGPoint birdPosition = ccp(self.contentSize.width / 4 + 24, self.contentSize.height / 2 + 16);
    activePipes = [[NSMutableArray alloc] initWithCapacity:4];
    
    for (int i = 0; i < 3; i ++) {
        PipePosition position = [self genPipePosition:320];
        Pipe *pipe = [[Pipe alloc] initWithType:PIPE_BLUE position:position scene:self birdPos:birdPosition.x];
        [activePipes addObject:pipe];
    }
    // footer
    footer = [[Footer alloc] initWithScene:self];
    REAL_HEIGHT = self.contentSize.height - footer.spr_1.position.y - footer.spr_1.contentSize.height / 2;
    // bird
    bird = [[Bird alloc] initWithType:arc4random_uniform(3) position:birdPosition scene:self];
    groundY = footer.spr_1.position.y + footer.spr_1.contentSize.height / 2 - 7;
    // score
    score = 0;
    scoreLabel = [CCLabelTTF labelWithString:@"0" fontName:@"Chalkduster" fontSize:30.0f];
    scoreLabel.positionType = CCPositionTypeNormalized;
    scoreLabel.color = [CCColor whiteColor];
    scoreLabel.position = ccp(0.5f, 0.85f);
    [self addChild:scoreLabel];
    // sound
    [[OALSimpleAudio sharedInstance] playBg:@"sfx_swooshing.caf" loop:YES];
    
    return self;
}

- (void)onBtnResumeClicked:(id)sender {
    NSString* userObject = (NSString*)btnResume.userObject;
    if ([userObject isEqualToString:@"pause"]) {
        [self pauseGame];
        [btnResume setUserObject:@"resume"];        
        [btnResume setBackgroundSpriteFrame:btnResumeFrame forState:CCControlStateNormal];
    } else {
        [self resumeGame];
        [btnResume setUserObject:@"pause"];
        [btnResume setBackgroundSpriteFrame:btnPauseFrame forState:CCControlStateNormal];
    }
}

-(PipePosition) genPipePosition:(float)pipeH {
    PipePosition position;
    float lastPosX = 0;
    for (int i = activePipes.count - 1; i >= 0; i --) {
        float pipeX = ((Pipe*)[activePipes objectAtIndex:i]).sprTop.position.x;
        if (pipeX > 0 && pipeX > lastPosX) {
            lastPosX = pipeX;
        }
    }
    // R = [70, 150]
    // ht = [10, Ht]
    // X = 150
    if (lastPosX == 0) {
        lastPosX = self.contentSize.width;
    }
    position.x = lastPosX + arc4random_uniform(50) + 200;
    position.top = drand48() * (pipeH - 60) + 60;
    position.range = drand48() * (120 - 55) + 55;
    if (position.top + position.range + pipeH < REAL_HEIGHT) {
        position.range = REAL_HEIGHT - (position.top + pipeH);
    }
    position.top = self.contentSize.height - position.top;
    return position;
}

-(void)increaseScore {
    score ++;
    scoreLabel.string = [NSString stringWithFormat:@"%i", score];
    [[OALSimpleAudio sharedInstance] playEffect:@"sfx_point.aif"];
}

-(void) update:(CCTime)delta {
    if (isPause) {
        return;
    }
    if (bird.state != BIRD_STATE_DIE) {
        [footer update:delta];
    }
	
    if (bird.state != BIRD_STATE_STAND && bird.state != BIRD_STATE_DIE) {
        // Ground collision detection
        float birdY = bird.sprBird.position.y - bird.sprBird.contentSize.height / 2 + 7;
        if (birdY <= groundY) {
            // collision
            if (bird.state != BIRD_STATE_FAIL) {
                [[OALSimpleAudio sharedInstance] playEffect:@"sfx_hit.caf"];
            }
            bird.sprBird.position = ccp(bird.sprBird.position.x, groundY + bird.sprBird.contentSize.height / 2);
            [bird doState:BIRD_STATE_DIE];
            [self handleGameOver];
            return;
        }
        if (bird.state != BIRD_STATE_FAIL) {
            // bird RECT
            CGRect birdRect = [bird getRect];
            for (int i = activePipes.count - 1; i >= 0; i--) {
                Pipe *curPipe = (Pipe*)[activePipes objectAtIndex:i];
                // Pipe collision detection
                CGRect curPipeTopRect = [curPipe getTopRect];
                CGRect curPipeBottomRect = [curPipe getBottomRect];
                if (CGRectIntersectsRect(birdRect, curPipeTopRect) || CGRectIntersectsRect(birdRect, curPipeBottomRect)){
                    [bird doState:BIRD_STATE_FAIL];
                    // sound
                    [[OALSimpleAudio sharedInstance] playEffect:@"sfx_hit.caf"];
                    return;
                }
                [curPipe update:delta];
            }
        }
        
        [bird update:delta];
    }
}

-(void) handleGameOver {
    // sound effect
    [[OALSimpleAudio sharedInstance] stopBg];
    [[OALSimpleAudio sharedInstance] playEffect:@"sfx_die.caf"];
    // remove pause/ resume button
    [self removeChild:btnResume cleanup:YES];
    // scene effect
    CCNodeColor *dieEffect = [CCNodeColor nodeWithColor:[CCColor colorWithWhite:1 alpha:0.5]];
    [dieEffect runAction:[CCActionTintTo actionWithDuration:0.65 color:[CCColor colorWithWhite:0 alpha:1]]];
    [self addChild:dieEffect];
    // Game Over Text and Score Panel
    NSArray* scorePanelInfo = [atlasInfo objectForKey:@"score_panel"];
    NSArray* gameOverTextInfo = [atlasInfo objectForKey:@"text_game_over"];
    CCSprite* sprScorePanel = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[scorePanelInfo objectAtIndex:3] intValue], [[scorePanelInfo objectAtIndex:4] intValue], [[scorePanelInfo objectAtIndex:1] intValue], [[scorePanelInfo objectAtIndex:2] intValue])];
    CCSprite* sprGameOverText = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[gameOverTextInfo objectAtIndex:3] intValue], [[gameOverTextInfo objectAtIndex:4] intValue], [[gameOverTextInfo objectAtIndex:1] intValue], [[gameOverTextInfo objectAtIndex:2] intValue])];
    float scorePanelY = self.contentSize.height / 2 - sprScorePanel.contentSize.height / 4;
    float gameOverTextY = scorePanelY + sprScorePanel.contentSize.height / 2 + sprGameOverText.contentSize.height / 2 + 30;
    sprGameOverText.position = ccp(self.contentSize.width / 2, gameOverTextY);
    sprScorePanel.position = ccp(self.contentSize.width / 2, -sprScorePanel.contentSize.height / 2);
    sprGameOverText.scale = 0.5;
    [sprGameOverText runAction:[CCActionScaleTo actionWithDuration:0.45 scale:1]];
    [sprScorePanel runAction:[CCActionMoveTo actionWithDuration:0.6 position:ccp(self.contentSize.width / 2, scorePanelY)]];
    
    int highest = [[NSUserDefaults standardUserDefaults] integerForKey:@"HAIDM_FB_SCORE"];
    if (score > 10) {
        int medalIndex = -1;
        if (score > highest) {
            medalIndex = 1;
        } else if (score + 10 >= highest) {
            medalIndex = 0;
        }
        if (medalIndex > -1) {            
            NSMutableArray* spriteFrames = [NSMutableArray array];
            for (int i = medalIndex; i < medalIndex + 3; i += 2) {
                NSString *resource = [NSString stringWithFormat:@"medals_%i", i];
                
                NSArray *medalInfo = [atlasInfo objectForKey:resource];
                CGSize medalSizeInPixels = CGSizeMake([[medalInfo objectAtIndex:1] intValue], [[medalInfo objectAtIndex:2] intValue]);
                CGRect medalRectInPixels = {CGPointMake([[medalInfo objectAtIndex:3] intValue], [[medalInfo objectAtIndex:4] intValue]), medalSizeInPixels};
                CCSpriteFrame* spriteFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:medalRectInPixels rotated:NO offset:CGPointZero originalSize:medalSizeInPixels];
                [spriteFrames addObject:spriteFrame];
            }
            CCSprite *medal = [CCSprite spriteWithSpriteFrame:[spriteFrames objectAtIndex:0]];
            medal.anchorPoint = ccp(0, 0.25);
            medal.position = ccp(31, 46);
            CCAnimation* animation = [CCAnimation animationWithSpriteFrames: spriteFrames delay:0.15];
            CCActionAnimate* actionAnimate = [CCActionAnimate actionWithAnimation:animation];
            [medal runAction:[CCActionRepeatForever actionWithAction:actionAnimate]];
            
            [sprScorePanel addChild:medal];
        }
    }
    if (score > highest) {
        highest = score;
        // save highest score
        [[NSUserDefaults standardUserDefaults] setInteger:highest forKey:@"HAIDM_FB_SCORE"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        // show new label
        NSArray *newInfo = [atlasInfo objectForKey:@"new"];
        CCSprite *sprNew = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[newInfo objectAtIndex:3] intValue], [[newInfo objectAtIndex:4] intValue], [[newInfo objectAtIndex:1] intValue], [[newInfo objectAtIndex:2] intValue])];
        sprNew.anchorPoint = ccp(0, 0.5);
        sprNew.position = ccp(140, 60);
        [sprScorePanel addChild:sprNew];
        // sound
        [[OALSimpleAudio sharedInstance] playEffect:@"sfx_point.aif"];
    }
    
    float scoreX = sprScorePanel.contentSize.width - 30;
    CCLabelTTF *userScoreLabel = [CCLabelTTF labelWithString:[NSString stringWithFormat:@"%i", score] fontName:@"Chalkduster" fontSize:20.0f];
    userScoreLabel.color = [CCColor whiteColor];
    userScoreLabel.anchorPoint = ccp(1, 0.5);
    userScoreLabel.position = ccp(scoreX, 80);
    CCLabelTTF *highestScoreLabel = [CCLabelTTF labelWithString:[NSString stringWithFormat:@"%i", highest] fontName:@"Chalkduster" fontSize:20.0f];
    highestScoreLabel.color = [CCColor whiteColor];
    highestScoreLabel.anchorPoint = ccp(1, 0.5);
    highestScoreLabel.position = ccp(scoreX, 36);
    [sprScorePanel addChild:userScoreLabel];
    [sprScorePanel addChild:highestScoreLabel];
    
    [self addChild:sprGameOverText];
    [self addChild:sprScorePanel];
    
    // btn Play
    NSArray *btnPlayInfo = [atlasInfo objectForKey:@"button_play"];
    CGSize btnSizeInPixels = CGSizeMake([[btnPlayInfo objectAtIndex:1] intValue], [[btnPlayInfo objectAtIndex:2] intValue]);
    CGRect btnPlayRectInPixels = {CGPointMake([[btnPlayInfo objectAtIndex:3] intValue], [[btnPlayInfo objectAtIndex:4] intValue]), btnSizeInPixels};
    CCSpriteFrame *btnPlayFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:btnPlayRectInPixels rotated:NO offset:CGPointZero originalSize:btnSizeInPixels];
    CCButton *btnPlay = [CCButton buttonWithTitle:@"" spriteFrame:btnPlayFrame];
    CGPoint btnPlayPosition = ccp(self.contentSize.width / 4, sprScorePanel.position.y - btnSizeInPixels.height);
    btnPlay.position = btnPlayPosition;
    [btnPlay setTarget:self selector:@selector(onBtnPlayClicked:)];    
    // btn Score
    NSArray *btnScoreInfo = [atlasInfo objectForKey:@"button_score"];
    CGRect btnScoreRectInPixels = {CGPointMake([[btnScoreInfo objectAtIndex:3] intValue], [[btnScoreInfo objectAtIndex:4] intValue]), btnSizeInPixels};
    CCSpriteFrame *btnScoreFrame = [CCSpriteFrame frameWithTexture:atlas rectInPixels:btnScoreRectInPixels rotated:NO offset:CGPointZero originalSize:btnSizeInPixels];
    CCButton *btnScore = [CCButton buttonWithTitle:@"" spriteFrame:btnScoreFrame];
    btnScore.position = ccp(self.contentSize.width - btnPlayPosition.x, btnPlayPosition.y);
    [btnScore setTarget:self selector:@selector(onBtnScoreClicked:)];
    
    float btnY = scorePanelY - sprScorePanel.contentSize.height / 2 - 10 - btnSizeInPixels.height / 2;
    [btnPlay runAction:[CCActionMoveTo actionWithDuration:0.62 position:ccp(btnPlay.position.x, btnY)]];
    [btnScore runAction:[CCActionMoveTo actionWithDuration:0.62 position:ccp(btnScore.position.x, btnY)]];
    
    [self addChild:btnPlay];
    [self addChild:btnScore];
}

- (void)touchBegan:(UITouch *)touch withEvent:(UIEvent *)event {
    if (bird.state == BIRD_STATE_STAND) {
        [self addChild:btnResume];
        [self removeChild:sprReadyText cleanup:true];
        [self removeChild:sprTutorial cleanup:true];
        [bird doState:BIRD_STATE_JUMP];
        [[OALSimpleAudio sharedInstance] playEffect:@"sfx_wing.caf"];
    } else if (!isPause && (bird.state != BIRD_STATE_DIE && bird.state != BIRD_STATE_FAIL)) {
        [bird doState:BIRD_STATE_JUMP];
        [[OALSimpleAudio sharedInstance] playEffect:@"sfx_wing.caf"];
    }
}

-(void) pauseGame {
    isPause = YES;
}

-(void) resumeGame {
    isPause = NO;
    if ((bird.state != BIRD_STATE_DIE && bird.state != BIRD_STATE_FAIL)) {
    	[bird doState:BIRD_STATE_JUMP];
    	[[OALSimpleAudio sharedInstance] playEffect:@"sfx_wing.caf"];
    }
}

- (void)onBtnPlayClicked:(id)sender {
    [[CCDirector sharedDirector] replaceScene:[PlayScene scene]];
    [self removeAllChildrenWithCleanup:YES];
    [self removeFromParent];
}

- (void)onBtnScoreClicked:(id)sender {
    // start spinning scene with transition
    NSLog(@"PlayScene BtnScore Clicked");
}

@end
