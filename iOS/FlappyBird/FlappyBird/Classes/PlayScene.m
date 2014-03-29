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
#import "Pipe.h"

@implementation PlayScene

NSMutableDictionary* atlasInfo;
CCTexture *atlas;

Footer* footer;
CCButton* btnResume;
CCSpriteFrame *btnResumeFrame, *btnPauseFrame;
CCSprite *sprReadyText, *sprTutorial;
Bird* bird;


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
    // footer
    footer = [[Footer alloc] initWithScene:self];
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
    // bird
    bird = [[Bird alloc] initWithType:BIRD_TYPE_YELLOW position:ccp(self.contentSize.width / 4 + 24, self.contentSize.height / 2 + 16) scene:self];
    
    return self;
}

- (void)onBtnResumeClicked:(id)sender {
    NSString* userObject = (NSString*)btnResume.userObject;
    if ([userObject isEqualToString:@"pause"]) {
        [btnResume setUserObject:@"resume"];        
        [btnResume setBackgroundSpriteFrame:btnResumeFrame forState:CCControlStateNormal];
    } else {
        [btnResume setUserObject:@"pause"];
        [btnResume setBackgroundSpriteFrame:btnPauseFrame forState:CCControlStateNormal];
    }
}

-(void) update:(CCTime)delta {
	[footer update:delta];
    [bird update:delta];
}

- (void)touchBegan:(UITouch *)touch withEvent:(UIEvent *)event {
    if (bird.state == BIRD_STATE_STAND) {
        [self addChild:btnResume];
        [self removeChild:sprReadyText cleanup:true];
        [self removeChild:sprTutorial cleanup:true];
        [bird doState:BIRD_STATE_DOWN];
    } else {
        CCLOG(@"Received a JUMP");
    }
}

@end
