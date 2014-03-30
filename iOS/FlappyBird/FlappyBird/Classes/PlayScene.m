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

float groundY;


+ (PlayScene *)scene {
    return [[self alloc] init];
}
- (id)init {
    self = [super init];
    if (!self) return(nil);
    
    score = 0;
    
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
    NSLog(@"increase score: %i", score);
}

-(void) pauseGame {
    isPause = YES;
}

-(void) resumeGame {
    isPause = NO;
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
                    return;
                }
                [curPipe update:delta];
            }
        }
        
        [bird update:delta];
    }
}

-(void) handleGameOver {
    NSLog(@"handling game over");
}

- (void)touchBegan:(UITouch *)touch withEvent:(UIEvent *)event {
    if (bird.state == BIRD_STATE_STAND) {
        [self addChild:btnResume];
        [self removeChild:sprReadyText cleanup:true];
        [self removeChild:sprTutorial cleanup:true];
        [bird doState:BIRD_STATE_JUMP];
    } else if (!isPause && bird.state != BIRD_STATE_DIE) {
        [bird doState:BIRD_STATE_JUMP];
    }
}

@end
